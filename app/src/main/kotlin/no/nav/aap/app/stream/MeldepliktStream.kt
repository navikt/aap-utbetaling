package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toKafkaDto
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.domene.utbetaling.modellapi.MeldepliktshendelseModellApi
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.kafka.streams.extension.*
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.meldepliktStream(mottakerKtable: KTable<String, MottakereKafkaDto>) {
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

private val håndter = { ident: String, meldepliktshendelseModellApi: MeldepliktshendelseModellApi, mottakerKafkaDto: MottakereKafkaDto ->
    val mottakerModellApi = mottakerKafkaDto.toModellApi()
    val observer = BehovObserver(ident)
    val endretMottakerModellApi = meldepliktshendelseModellApi.håndter(mottakerModellApi, observer)
    endretMottakerModellApi.toKafkaDto(mottakerKafkaDto.sekvensnummer) to observer.behovene()
}
