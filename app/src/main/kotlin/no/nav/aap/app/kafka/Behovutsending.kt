package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.hendelse.DtoBehov
import no.nav.aap.domene.utbetaling.hendelse.Lytter
import org.apache.kafka.streams.kstream.Branched
import org.apache.kafka.streams.kstream.BranchedKStream
import org.apache.kafka.streams.kstream.KStream

internal fun KStream<String, DtoBehov>.sendBehov(name: String, topics: Topics) {
    split(named("$name-split-behov"))
        .branch(topics.barn, "$name-medlem", DtoBehov::erBarn, ::ToAvroBarn)
        .branch(topics.institusjon, "$name-inntekter", DtoBehov::erInstitusjon, ::ToAvroInstitusjon)
}

private fun <AVROVALUE : Any, MAPPER> BranchedKStream<String, DtoBehov>.branch(
    topic: Topic<String, AVROVALUE>,
    branchName: String,
    predicate: (DtoBehov) -> Boolean,
    getMapper: () -> MAPPER
) where MAPPER : ToAvro<AVROVALUE>, MAPPER : Lytter =
    branch({ _, value -> predicate(value) }, Branched.withConsumer<String?, DtoBehov?> { chain ->
        chain
            .mapValues(named("branch-$branchName-map-behov")) { value -> getMapper().also(value::accept).toAvro() }
            .to(topic, topic.produced("branch-$branchName-produced-behov"))
    }.withName("-branch-$branchName"))

private interface ToAvro<out AVROVALUE> {
    fun toAvro(): AVROVALUE
}

private class ToAvroBarn : Lytter, ToAvro<AvroBarna> {
    private lateinit var ident: String
    override fun toAvro(): AvroBarna {
        TODO("Not yet implemented")
    }
}

private class ToAvroInstitusjon : Lytter, ToAvro<AvroInstitusjon> {
    override fun toAvro(): AvroInstitusjon {
        TODO("Not yet implemented")
    }
}
