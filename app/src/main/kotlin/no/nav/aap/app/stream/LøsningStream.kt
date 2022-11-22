package no.nav.aap.app.stream

import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toKafkaDto
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.filterNotNullBy
import no.nav.aap.kafka.streams.extension.join
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.løsningStream(mottakerKtable: KTable<String, MottakereKafkaDto>) {
    consume(Topics.utbetalingsbehov)
        .filterNotNullBy("losning-filter-tombstones-og-responses") { løsning -> løsning.response }
        .join(Topics.utbetalingsbehov with Topics.mottakere, mottakerKtable, håndter)
        .produce(Topics.mottakere, "produced-mottakere-for-losning")
}

private val håndter = { løsning: KafkaUtbetalingsbehov, mottakerKafkaDto: MottakereKafkaDto ->
    val mottakerModellApi = mottakerKafkaDto.toModellApi()
    val response = requireNotNull(løsning.response) { "Hendelse uten response må filtreres bort" }
    val endretMottaker = response.barn.håndter(mottakerModellApi)
    endretMottaker.toKafkaDto(mottakerKafkaDto.sekvensnummer)
}
