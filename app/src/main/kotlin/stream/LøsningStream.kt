package stream

import kafka.Topics
import kafka.buffer
import kafka.toModellApi
import kafka.toMottakereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.MottakereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology
import stream.mock.UtbetalingsbehovMock

internal fun Topology.løsningStream(mottakerKtable: KTable<MottakereKafkaDtoHistorikk>) {
    consume(Topics.utbetalingsbehov)
        .branch({ løsning -> løsning.response != null }) { stream ->
            stream.joinWith(mottakerKtable)
                .map { løsning, mottakereHistorikk ->
                    val mottakerModellApi = mottakereHistorikk.mottakereKafkaDto.toModellApi()
                    val response = requireNotNull(løsning.response) { "Hendelse uten response må filtreres bort" }
                    val endretMottaker = response.barn.håndter(mottakerModellApi)
                    val gammelSekvensnummer = mottakereHistorikk.sekvensnummer
                    endretMottaker.toMottakereKafkaDtoHistorikk(gammelSekvensnummer)
                }
                .produce(Topics.mottakere, mottakerKtable.buffer) { it }
        }
        .branch(UtbetalingsbehovMock::manglerResponse, UtbetalingsbehovMock::mockResponseStream)
}
