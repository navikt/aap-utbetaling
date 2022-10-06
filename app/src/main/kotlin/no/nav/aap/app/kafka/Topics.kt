package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Table
import no.nav.aap.kafka.streams.Topic

internal object Topics {
    val mottakere = Topic("aap.mottakere.v1", JsonSerde.jackson<DtoMottaker>())
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<IverksettVedtakKafkaDto>())
    val meldeplikt = Topic("aap.meldeplikt.v1", JsonSerde.jackson<DtoMeldepliktshendelse>())
    // TODO Hvordan løser vi samlede løsninger
    val utbetalingsbehov = Topic("aap.utbetalingsbehov.v1", JsonSerde.jackson<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov>())
}

internal object Tables {
    val mottakere = Table("mottakere", Topics.mottakere)
}
