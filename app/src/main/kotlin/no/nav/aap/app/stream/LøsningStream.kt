package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoLøsning
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

fun StreamsBuilder.løsningStream(mottakerKtable: KTable<String, DtoMottaker>) {
    consume(Topics.løsning)
        .filterNotNull { "filter-losning-tombstone" }
        .join(Topics.løsning with Topics.mottakere, mottakerKtable, ::LøsningOgMottak)
        .mapValues { _, (løsning, dtoMottaker) ->
            val mottaker = Mottaker.gjenopprett(dtoMottaker)
            mottaker.håndterLøsning(løsning.opprettLøsning())
            mottaker.toDto()
        }
        .produce(Topics.mottakere) { "produced-mottakere-for-losning" }
}

private data class LøsningOgMottak(
    val løsning: DtoLøsning,
    val mottaker: DtoMottaker
)
