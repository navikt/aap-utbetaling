package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.løsningStream(mottakerKtable: KTable<String, DtoMottaker>) {
    consume(Topics.utbetalingsbehov)
        .filterNotNull("filter-losning-tombstone")
        .filter("filter-losning-response") { _, løsning -> løsning.response != null }
        .join(Topics.utbetalingsbehov with Topics.mottakere, mottakerKtable, ::Pair)
        .mapValues { _, (løsning, dtoMottaker) ->
            val response = requireNotNull(løsning.response) { "Hendelse uten response må filtreres bort" }
            val mottaker = Mottaker.gjenopprett(dtoMottaker)
            mottaker.håndterLøsning(response.barn.opprettLøsning())
            mottaker.toDto()
        }
        .produce(Topics.mottakere, "produced-mottakere-for-losning")
}
