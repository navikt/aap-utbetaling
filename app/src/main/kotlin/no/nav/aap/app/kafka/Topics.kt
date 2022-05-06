package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.dto.DtoMeldeplikt
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.dto.DtoVedtakshendelse
import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Table
import no.nav.aap.kafka.streams.Topic

object Topics {
    val mottakere = Topic("aap.mottakere.v1", JsonSerde.jackson<DtoMottaker>())
    val vedtak = Topic("aap.vedtak.v1", JsonSerde.jackson<DtoVedtakshendelse>())
    val meldeplikt = Topic("aap.meldeplikt.v1", JsonSerde.jackson<DtoMeldeplikt>())
    // TODO Oppdrag og barn/institusjon
}

object Tables {
    val mottakere = Table("mottakere", Topics.mottakere)
}
