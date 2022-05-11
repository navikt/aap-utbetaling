package no.nav.aap.app.stream

import no.nav.aap.app.kafka.KafkaUtbetalingsbehov
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoBehov
import no.nav.aap.domene.utbetaling.hendelse.Lytter
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.named
import no.nav.aap.kafka.streams.produce
import org.apache.kafka.streams.kstream.Branched
import org.apache.kafka.streams.kstream.BranchedKStream
import org.apache.kafka.streams.kstream.KStream

internal fun KStream<String, DtoBehov>.sendBehov(name: String) {
    split(named("$name-split-behov"))
        .branch(Topics.utbetalingsbehov, "$name-barn", DtoBehov::erBarn, ::ToKafkaUtbetalingsbehov)
}

private fun <JSON : Any, MAPPER> BranchedKStream<String, DtoBehov>.branch(
    topic: Topic<JSON>,
    branchName: String,
    predicate: (DtoBehov) -> Boolean,
    getMapper: (String) -> MAPPER
) where MAPPER : ToKafka<JSON>, MAPPER : Lytter =
    branch({ _, value -> predicate(value) }, Branched.withConsumer<String?, DtoBehov?> { chain ->
        chain
            .mapValues(
                { key, value -> getMapper(key).also(value::accept).toJson() },
                named("branch-$branchName-map-behov")
            )
            .produce(topic) { "branch-$branchName-produced-behov" }
    }.withName("-branch-$branchName"))

private interface ToKafka<out JSON> {
    fun toJson(): JSON
}

private class ToKafkaUtbetalingsbehov(private val ident: String) : Lytter, ToKafka<KafkaUtbetalingsbehov> {
    override fun toJson() = KafkaUtbetalingsbehov(KafkaUtbetalingsbehov.Request(ident), null)
}
