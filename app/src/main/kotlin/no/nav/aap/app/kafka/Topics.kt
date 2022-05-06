package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.dto.DtoLøsning
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.dto.DtoVedtakshendelse
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Table
import no.nav.aap.kafka.streams.Topic

object Topics {
    val mottakere = Topic("aap.mottakere.v1", JsonSerde.jackson<DtoMottaker>())
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<DtoVedtakshendelse>())
    val meldeplikt = Topic("aap.meldeplikt.v1", JsonSerde.jackson<DtoMeldepliktshendelse>())
    // TODO Hvordan løser vi samlede løsninger
    val løsning = Topic("aap.losning.v1", JsonSerde.jackson<DtoLøsning>())
}

object Tables {
    val mottakere = Table("mottakere", Topics.mottakere)
}
