package no.nav.aap.app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.app.kafka.Tables
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.stream.løsningStream
import no.nav.aap.app.stream.meldepliktStream
import no.nav.aap.app.stream.mock.meldepliktStreamMock
import no.nav.aap.app.stream.mock.utbetalingsbehovStreamMock
import no.nav.aap.app.stream.vedtakStream
import no.nav.aap.kafka.streams.*
import no.nav.aap.kafka.streams.store.scheduleMetrics
import no.nav.aap.ktor.config.loadConfig
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.minutes

private val secureLog = LoggerFactory.getLogger("secureLog")

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

internal fun Application.server(kafka: KStreams = KafkaStreams) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    kafka.connect(
        config = config.kafka,
        registry = prometheus,
        topology = topology(prometheus),
    )

    routing {
        actuator(prometheus, kafka)
    }
}

internal fun topology(registry: MeterRegistry): Topology = StreamsBuilder().apply {
    val mottakerKtable = consume(Topics.mottakere)
        .filterNotNull("filter-mottakere-tombstone")
        .produce(Tables.mottakere)

    mottakerKtable.scheduleMetrics(Tables.mottakere, 2.minutes, registry)

    vedtakStream(mottakerKtable)
    meldepliktStream(mottakerKtable)
    løsningStream(mottakerKtable)

    meldepliktStreamMock()
    utbetalingsbehovStreamMock()
}.build()

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
