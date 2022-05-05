package no.nav.aap.app

import no.nav.aap.kafka.streams.test.KafkaStreamsMock
import org.apache.kafka.streams.*

class Mocks : AutoCloseable {
    val kafka = KafkaStreamsMock()

    override fun close() {
        kafka.close()
    }
}

inline fun <reified V : Any> TestInputTopic<String, V>.produce(key: String, value: () -> V) = pipeInput(key, value())
