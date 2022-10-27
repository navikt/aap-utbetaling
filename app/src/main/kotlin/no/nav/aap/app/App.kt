package no.nav.aap.app

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.simulering.SimuleringRequest
import no.nav.aap.app.stream.løsningStream
import no.nav.aap.app.stream.meldepliktStream
import no.nav.aap.app.stream.mock.utbetalingsbehovStreamMock
import no.nav.aap.app.stream.vedtakStream
import no.nav.aap.domene.utbetaling.dto.*
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.kafka.streams.KStreams
import no.nav.aap.kafka.streams.KafkaStreams
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.produce
import no.nav.aap.kafka.streams.store.migrateStateStore
import no.nav.aap.kafka.streams.store.scheduleMetrics
import no.nav.aap.kafka.vanilla.KafkaConfig
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import java.util.*
import kotlin.time.Duration.Companion.minutes

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

internal fun Application.server(kafka: KStreams = KafkaStreams) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()
    val mottakerProducer = kafka.createProducer(KafkaConfig.copyFrom(config.kafka), Topics.mottakere)

    install(MicrometerMetrics) { registry = prometheus }
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) {
        kafka.close()
        mottakerProducer.close()
    }

    kafka.connect(
        config = config.kafka,
        registry = prometheus,
        topology = topology(prometheus, mottakerProducer),
    )

    routing {
        simulering()
        actuator(prometheus, kafka)
    }
}

internal fun topology(registry: MeterRegistry, mottakerProducer: Producer<String, MottakereKafkaDto>): Topology {
    val streams = StreamsBuilder()
    val mottakerKtable = streams
        // Setter timestamp for mottakere tilbake ett år for å tvinge topologien å oppdatere tabellen før neste hendelse leses
        .consume(Topics.mottakere, { record, _ -> record.timestamp() - 365L * 24L * 3600L * 1000L })
        .produce(Tables.mottakere)

    mottakerKtable.scheduleMetrics(Tables.mottakere, 2.minutes, registry)
    mottakerKtable.migrateStateStore(Tables.mottakere, mottakerProducer)

    streams.vedtakStream(mottakerKtable)
    streams.meldepliktStream(mottakerKtable)
    streams.løsningStream(mottakerKtable)

    streams.utbetalingsbehovStreamMock()

    return streams.build()
}

private fun Routing.simulering() {
    route("/simuler") {
        post("/{personident}") {
            val personident = requireNotNull(call.parameters["personident"]) {"Personident må være satt"}
            val simuleringRequest = call.receive<SimuleringRequest>()
            val mottaker = DtoMottaker.opprettMottaker(personident, simuleringRequest.fødselsdato)
            val vedtakshendelse = DtoVedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                fødselsdato = simuleringRequest.fødselsdato,
                innvilget = simuleringRequest.innvilget,
                grunnlagsfaktor = simuleringRequest.grunnlagsfaktor,
                vedtaksdato = simuleringRequest.vedtaksdato,
                virkningsdato = simuleringRequest.virkningsdato
            )
            vedtakshendelse.håndter(mottaker)
            val meldepliktshendelse = DtoMeldepliktshendelse(
                aktivitetPerDag = listOf(DtoAkivitetPerDag(
                    dato = simuleringRequest.virkningsdato,
                    arbeidstimer = 0.0,
                    fraværsdag = false
                ))
            )
            meldepliktshendelse.håndter(mottaker, object : DtoMottakerObserver{})
            // ???
            // Profit
            call.respondText("OK")
        }
    }
}

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: KStreams) {
    route("/actuator") {
        get("/metrics") {
            call.respondText { prometheus.scrape() }
        }
        get("/live") {
            val status = if (kafka.isLive()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("utbetaling", status = status)
        }
        get("/ready") {
            val status = if (kafka.isReady()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("utbetaling", status = status)
        }
    }
}
