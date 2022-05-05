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
import no.nav.aap.kafka.KafkaConfig
import no.nav.aap.kafka.streams.KStreams
import no.nav.aap.kafka.streams.KafkaStreams
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
/*
    kafka.start(config.kafka, prometheus) {
        createTopology()
    }*/

    routing {
        actuator(prometheus, kafka)
    }
}

internal fun createTopology(): StreamsBuilder = StreamsBuilder().apply {

}

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: KStreams) {
    route("/actuator") {
        get("/metrics") {
            call.respond(prometheus.scrape())
        }
        get("/live") {
            //val status = if (kafka.isLive()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(HttpStatusCode.OK, "utbetaling")
        }
        get("/ready") {
            //val status = if (kafka.isReady()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respond(HttpStatusCode.OK, "utbetaling")
        }
    }
}
