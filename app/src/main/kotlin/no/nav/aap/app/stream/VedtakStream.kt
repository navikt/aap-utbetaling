package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.kafka.streams.consume
import no.nav.aap.kafka.streams.filterNotNull
import no.nav.aap.kafka.streams.leftJoin
import no.nav.aap.kafka.streams.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.vedtakStream(mottakerKtable: KTable<String, DtoMottaker>) {
    consume(Topics.vedtak)
        .filterNotNull("filter-vedtakshendelse-tombstone")
        .leftJoin(Topics.vedtak with Topics.mottakere, mottakerKtable, ::Pair)
        .mapValues { key, (dtoVedtakshendelse, dtoMottaker) ->
            val mottaker = dtoMottaker?.let { Mottaker.gjenopprett(it) } ?: Mottaker(
                Personident(key),
                Fødselsdato(dtoVedtakshendelse.fødselsdato)
            )
            mottaker.håndterVedtak(Vedtakshendelse.gjenopprett(dtoVedtakshendelse))
            mottaker.toDto()
        }
        .produce(Topics.mottakere, "produced-mottakere-for-vedtak")
}
