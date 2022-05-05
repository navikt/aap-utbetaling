package no.nav.aap.app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.dto.DtoVedtak
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.streams.*
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.streams.StreamsBuilder
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

data class Config(
    val kafka: KafkaConfig
)

internal fun Application.server(kafka: KStreams = KafkaStreams) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    kafka.start(config.kafka, prometheus) {
        val mottakerKtable = consume(Topics.mottakere).filterNotNull { "filter-mottakere-tombstone" }.produce(Tables.mottakere)

        consume(Topics.vedtak)
            .filterNotNull { "filter-vedtak-tombstone" }
            .leftJoin(Topics.vedtak with Topics.mottakere, mottakerKtable, ::VedtakOgMottak)
            .mapValues { _, value ->
                val mottaker = value.mottaker?.let { Mottaker.gjenopprett(it) } ?: Mottaker()
                mottaker.håndterVedtak(Vedtakshendelse.gjenopprett(value.vedtak))
                mottaker.toDto()
            }
            .produce(Topics.mottakere) {"produced-mottakere"}
    }

    routing {
        actuator(prometheus, kafka)
    }
}
/*
internal fun createTopology(): StreamsBuilder = StreamsBuilder().apply {
    val mottakerKtable = consume(Topics.mottakere).filterNotNull { "filter-mottakere-tombstone" }.produce(Tables.mottakere)

    consume(Topics.vedtak)
        .filterNotNull { "filter-vedtak-tombstone" }
        .leftJoin(Topics.vedtak with Topics.mottakere, mottakerKtable, ::VedtakOgMottak)
        .mapValues { _, value ->
            val mottaker = value.mottaker?.let { Mottaker.gjenopprett(it) } ?: Mottaker()
            mottaker.håndterVedtak(Vedtakshendelse.gjenopprett(value.vedtak))
            mottaker.toDto()
        }
        .produce(Topics.mottakere) {"produced-mottakere"}
}*/

data class VedtakOgMottak(
    val vedtak: DtoVedtak,
    val mottaker: DtoMottaker?
)

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: KStreams) {
    route("/actuator") {
        get("/metrics") {
            call.respond(prometheus.scrape())
        }
        get("/live") {
            val status = if (kafka.isLive()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(status, "utbetaling")
        }
        get("/ready") {
            val status = if (kafka.isReady()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(status, "utbetaling")
        }
    }
}
