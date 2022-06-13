package no.nav.aap.app.stream.mock

import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.named
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.slf4j.LoggerFactory

private val secureLog = LoggerFactory.getLogger("secureLog")

fun <V> StreamsBuilder.mockConsume(topic: Topic<V>): KStream<String, V?> =
    stream(
        topic.name,
        Consumed
            .with(topic.keySerde, topic.valueSerde)
            .withName("mock-consume-${topic.name}")
    ).peek(
        { key, value -> secureLog.info("consumed [${topic.name}] K:$key V:$value") },
        named("mock-log-consume-${topic.name}")
    )
