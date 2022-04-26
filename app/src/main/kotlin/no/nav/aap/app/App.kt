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
import no.nav.aap.app.config.Config
import no.nav.aap.app.config.loadConfig
import no.nav.aap.app.kafka.KStreams
import no.nav.aap.app.kafka.Kafka
import no.nav.aap.app.kafka.Topics
import org.apache.kafka.streams.KafkaStreams.State
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::server).start(wait = true)
}

internal fun Application.server(kafka: Kafka = KStreams()) {
    val prometheus = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val config = loadConfig<Config>()

    install(MicrometerMetrics) { registry = prometheus }

    Thread.currentThread().setUncaughtExceptionHandler { _, e -> log.error("Uhåndtert feil", e) }
    environment.monitor.subscribe(ApplicationStopping) { kafka.close() }

    val topics = Topics(config.kafka)
    val topology = createTopology(topics)
    kafka.start(topology, config.kafka, prometheus)

    routing {
        actuator(prometheus, kafka)
    }
}

internal fun createTopology(topics: Topics): Topology = StreamsBuilder().apply {

}.build()

private fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: Kafka) {
    route("/actuator") {
        get("/metrics") { call.respond(prometheus.scrape()) }
        get("/live") {
            val status = if (kafka.state() == State.ERROR) HttpStatusCode.InternalServerError else HttpStatusCode.OK
            call.respond(status, "vedtak")
        }
        get("/ready") {
            val healthy = kafka.state() in listOf(State.CREATED, State.RUNNING, State.REBALANCING)
            when (healthy && kafka.started()) {
                true -> call.respond(HttpStatusCode.OK, "vedtak")
                false -> call.respond(HttpStatusCode.InternalServerError, "vedtak")
            }
        }
    }
}
