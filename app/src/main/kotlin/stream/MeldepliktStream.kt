package stream

import kafka.Topics
import kafka.buffer
import kafka.toModellApi
import kafka.toMottakereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.MottakereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Topology

internal fun Topology.meldepliktStream(mottakerKtable: KTable<MottakereKafkaDtoHistorikk>) {
    val hendelseHåndtert = consume(Topics.meldeplikt)
        .joinWith(mottakerKtable, mottakerKtable.buffer)
        .map { meldepliktshendelseModellApi, mottakereKafkaDtoHistorikk ->
            val mottakerModellApi = mottakereKafkaDtoHistorikk.mottakereKafkaDto.toModellApi()
            val observer = BehovObserver()
            val endretMottakerModellApi = meldepliktshendelseModellApi.håndter(mottakerModellApi, observer)
            val gammeltSekvensnummer = mottakereKafkaDtoHistorikk.sekvensnummer
            endretMottakerModellApi.toMottakereKafkaDtoHistorikk(gammeltSekvensnummer) to observer.behovene()
        }

    hendelseHåndtert
        .map(Pair<MottakereKafkaDtoHistorikk, List<BehovUtbetaling>>::first)
        .produce(Topics.mottakere, mottakerKtable.buffer) { it }

    hendelseHåndtert
        .flatMap(Pair<MottakereKafkaDtoHistorikk, List<BehovUtbetaling>>::second)
        .branch(BehovUtbetaling::erUtbetalingsbehov) { branch ->
            branch
                .map { value -> UtbetalingsbehovVisitor().also(value::accept).toJson() }
                .produce(Topics.utbetalingsbehov)
        }
}
