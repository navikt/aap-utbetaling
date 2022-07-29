package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.meldepliktStream(mottakerKtable: KTable<String, DtoMottaker>) {
    val hendelseHåndtert = consume(Topics.meldeplikt)
        .filterNotNull("meldepliktshendelse-filter-tombstone")
        .join(Topics.meldeplikt with Topics.mottakere, mottakerKtable, håndter)

    hendelseHåndtert
        .firstPairValue("meldeplikt-hent-ut-mottaker")
        .produce(Topics.mottakere, "produced-mottakere-for-meldeplikt")

    hendelseHåndtert
        .secondPairValue("meldeplikt-hent-ut-behov")
        .flatten("meldeplikt-flatten-behov")
        .sendBehov("meldeplikt")
}

private val håndter = { ident: String, dtoMeldeplikt: DtoMeldepliktshendelse, dtoMottaker: DtoMottaker ->
    val mottaker = Mottaker.gjenopprett(dtoMottaker)
    val observer = BehovObserver(ident)
    mottaker.registerObserver(observer)
    val meldepliktshendelse = dtoMeldeplikt.opprettMeldepliktshendelse()
    mottaker.håndterMeldeplikt(meldepliktshendelse)
    mottaker.toDto() to observer.behovene()
}
