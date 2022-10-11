package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toDto
import no.nav.aap.app.kafka.toJson
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
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

private val håndter = { dtoMeldeplikt: DtoMeldepliktshendelse, mottakerKafkaDto: MottakereKafkaDto ->
    val dtoMottaker = mottakerKafkaDto.toDto()
    val observer = BehovObserver(mottakerKafkaDto.personident)
    val endretDtoMottaker = dtoMeldeplikt.håndter(dtoMottaker, observer)
    endretDtoMottaker.toJson(mottakerKafkaDto.sekvensnummer) to observer.behovene()
}
