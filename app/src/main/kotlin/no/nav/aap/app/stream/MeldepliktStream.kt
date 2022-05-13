package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.kafka.streams.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.meldepliktStream(mottakerKtable: KTable<String, DtoMottaker>) {
    val hendelseHåndtert = consume(Topics.meldeplikt)
        .filterNotNull("filter-meldepliktshendelse-tombstone")
        .join(Topics.meldeplikt with Topics.mottakere, mottakerKtable, ::Pair)
        .mapValues { ident, (dtoMeldepliktshendelse, dtoMottaker) ->
            val mottaker = Mottaker.gjenopprett(dtoMottaker)
            val observer = BehovObserver(ident)
            mottaker.registerObserver(observer)
            val meldepliktshendelse = dtoMeldepliktshendelse.opprettMeldepliktshendelse()
            mottaker.håndterMeldeplikt(meldepliktshendelse)
            observer.behovene() to mottaker.toDto()
        }

    hendelseHåndtert
        .mapValues("meldeplikt-hent-ut-mottaker") { (_, mottaker) -> mottaker }
        .produce(Topics.mottakere, "produced-mottakere-for-meldeplikt")

    hendelseHåndtert
        .flatMapValues("meldeplikt-hent-ut-behov") { (behov, _) -> behov }
        .sendBehov("meldeplikt")
}
