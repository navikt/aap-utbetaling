package app.kafka

import no.nav.aap.domene.utbetaling.modellapi.MeldepliktshendelseModellApi
import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDtoHistorikk
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.Table
import no.nav.aap.kafka.streams.v2.Topic
import no.nav.aap.kafka.streams.v2.concurrency.RaceConditionBuffer
import no.nav.aap.kafka.streams.v2.serde.JsonSerde

internal object Topics {
    val mottakere = Topic(
        name = "aap.mottakere.v1",
        valueSerde = JsonSerde.jackson(
            dtoVersion = MottakereKafkaDto.VERSION,
            migrate = ForrigeMottakereKafkaDtoHistorikk::toKafkaDto
        ) { json -> json.get("mottakereKafkaDto")?.get("version")?.takeIf { it.isNumber }?.intValue() },
    )
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<IverksettVedtakKafkaDto>())
    val meldeplikt = Topic("aap.meldeplikt.v1", JsonSerde.jackson<MeldepliktshendelseModellApi>())

    // TODO Hvordan løser vi samlede løsninger
    val utbetalingsbehov = Topic("aap.utbetalingsbehov.v1", JsonSerde.jackson<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov>())
}

val KTable<MottakereKafkaDtoHistorikk>.buffer by lazy {
    RaceConditionBuffer<MottakereKafkaDtoHistorikk>(logRecordValues = true)
}

internal object Tables {
    val mottakere = Table(Topics.mottakere)
}
