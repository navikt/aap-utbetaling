package app

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import app.kafka.Tables
import app.kafka.Topics
import no.nav.aap.dto.kafka.MottakereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KStreams
import no.nav.aap.kafka.streams.v2.KafkaStreams
import no.nav.aap.kafka.streams.v2.Topology
import no.nav.aap.kafka.streams.v2.processor.state.GaugeStoreEntriesStateScheduleProcessor
import no.nav.aap.kafka.streams.v2.processor.state.MigrateStateInitProcessor
import no.nav.aap.kafka.streams.v2.topology
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.clients.producer.Producer
import app.stream.løsningStream
import app.stream.meldepliktStream
import app.stream.vedtakStream
import kotlin.time.Duration.Companion.minutes

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

internal fun Application.server(kafka: KStreams = KafkaStreams()) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()
    val mottakerProducer = kafka.createProducer(config.kafka, Topics.mottakere)

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

internal fun topology(
    registry: MeterRegistry,
    mottakerProducer: Producer<String, MottakereKafkaDtoHistorikk>,
): Topology = topology {
    val mottakerKtable =
        consume(Tables.mottakere)

    mottakerKtable.schedule(
        GaugeStoreEntriesStateScheduleProcessor(
            ktable = mottakerKtable,
            interval = 2.minutes,
            registry = registry,
        )
    )

    mottakerKtable.init(
        MigrateStateInitProcessor(
            ktable = mottakerKtable,
            producer = mottakerProducer,
            logValue = true,
        )
    )

    vedtakStream(mottakerKtable)
    meldepliktStream(mottakerKtable)
    løsningStream(mottakerKtable)
}

