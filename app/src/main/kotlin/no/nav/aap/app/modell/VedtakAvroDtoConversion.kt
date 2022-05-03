package no.nav.aap.app.modell

import no.nav.aap.app.kafka.KafkaVedtak
import no.nav.aap.domene.utbetaling.dto.DtoVedtak

fun KafkaVedtak.toDto() = DtoVedtak(
    vedtaksid = this.vedtaksid,
    innvilget = this.innvilget,
    grunnlagsfaktor = this.grunnlagsfaktor,
    vedtaksdato = this.vedtaksdato,
    virkningsdato = this.virkningsdato,
    fødselsdato = this.fødselsdato
)