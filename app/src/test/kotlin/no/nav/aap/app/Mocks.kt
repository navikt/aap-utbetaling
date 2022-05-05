package no.nav.aap.app

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import no.nav.aap.kafka.streams.test.KafkaStreamsMock
import org.apache.kafka.streams.*

class Mocks : AutoCloseable {
    val kafka = KafkaStreamsMock()

    companion object {
        val NettyApplicationEngine.port get() = runBlocking { resolvedConnectors() }.first { it.type == ConnectorType.HTTP }.port
    }

    override fun close() {
        kafka.close()
    }
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) = pipeInput(key, value())
