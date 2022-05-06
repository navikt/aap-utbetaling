package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

fun StreamsBuilder.meldepliktStream(mottakerKtable: KTable<String, DtoMottaker>) {
    consume(Topics.meldeplikt)
        .filterNotNull { "filter-meldepliktshendelse-tombstone" }
        .join(Topics.meldeplikt with Topics.mottakere, mottakerKtable, ::MeldepliktOgMottak)
        .mapValues { _, (dtoMeldepliktshendelse, dtoMottaker) ->
            val mottaker = Mottaker.gjenopprett(dtoMottaker)
            mottaker.håndterMeldeplikt(dtoMeldepliktshendelse.opprettMeldepliktshendelse())
            mottaker.toDto()
        }
        .produce(Topics.mottakere) { "produced-mottakere-for-meldeplikt" }
}

private data class MeldepliktOgMottak(
    val meldepliktshendelse: DtoMeldepliktshendelse,
    val mottaker: DtoMottaker
)