package no.nav.aap.app

import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import no.nav.aap.kafka.streams.test.KafkaStreamsMock
import org.apache.kafka.streams.TestInputTopic

fun withTestApp(test: ApplicationTestBuilder.(mocks: MockEnvironment) -> Unit) = MockEnvironment().use { mocks ->
    testApplication {
        environment { config = mocks.containerProperties }
        application {
            server(mocks.kafka)
            runBlocking { this@testApplication.test(mocks) }
        }
    }
}

class MockEnvironment : AutoCloseable {
    val kafka = KafkaStreamsMock()

    val containerProperties = MapApplicationConfig(
        "KAFKA_STREAMS_APPLICATION_ID" to "utbetaling",
        "KAFKA_BROKERS" to "mock://kafka",
        "KAFKA_TRUSTSTORE_PATH" to "",
        "KAFKA_SECURITY_ENABLED" to "false",
        "KAFKA_KEYSTORE_PATH" to "",
        "KAFKA_CREDSTORE_PASSWORD" to "",
        "KAFKA_CLIENT_ID" to "utbetaling"
    )

    override fun close() {

    }

    companion object {
        val NettyApplicationEngine.port get() = runBlocking { resolvedConnectors() }.first { it.type == ConnectorType.HTTP }.port
    }
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) = pipeInput(key, value())
