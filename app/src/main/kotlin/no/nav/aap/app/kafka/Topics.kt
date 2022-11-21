package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.modellapi.MeldepliktshendelseModellApi
import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.BufferableTopic
import no.nav.aap.kafka.streams.Table
import no.nav.aap.kafka.streams.Topic
import no.nav.aap.kafka.streams.concurrency.RaceConditionBuffer

internal object Topics {
    private val buffer = RaceConditionBuffer<String, MottakereKafkaDto>(logRecordValues = true)

    val mottakere = BufferableTopic("aap.mottakere.v1", JsonSerde.jackson(MottakereKafkaDto.VERSION, ForrigeMottakereKafkaDto::toKafkaDto), buffer)
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<IverksettVedtakKafkaDto>())
    val meldeplikt = Topic("aap.meldeplikt.v1", JsonSerde.jackson<MeldepliktshendelseModellApi>())
    // TODO Hvordan løser vi samlede løsninger
    val utbetalingsbehov = Topic("aap.utbetalingsbehov.v1", JsonSerde.jackson<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov>())
}

internal object Tables {
    val mottakere = Table("mottakere", Topics.mottakere)
}
