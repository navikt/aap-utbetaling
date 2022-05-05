package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.dto.DtoVedtak
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

fun StreamsBuilder.vedtakStream(mottakerKtable: KTable<String, DtoMottaker>) {
    consume(Topics.vedtak)
        .filterNotNull { "filter-vedtak-tombstone" }
        .leftJoin(Topics.vedtak with Topics.mottakere, mottakerKtable, ::VedtakOgMottak)
        .mapValues { _, value ->
            val mottaker = value.mottaker?.let { Mottaker.gjenopprett(it) } ?: Mottaker()
            mottaker.håndterVedtak(Vedtakshendelse.gjenopprett(value.vedtak))
            mottaker.toDto()
        }
        .produce(Topics.mottakere) {"produced-mottakere"}
}

private data class VedtakOgMottak(
    val vedtak: DtoVedtak,
    val mottaker: DtoMottaker?
)
