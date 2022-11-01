package no.nav.aap.app.simulering

import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.domene.utbetaling.dto.DtoVedtakshendelse
import java.time.LocalDate
import java.util.*

data class SimuleringRequest(
    val fødselsdato: LocalDate,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val aktivitetsdager: List<AktivitetDag>
) {
    data class AktivitetDag(
        val dato: LocalDate,
        val arbeidstimer: Double,
        val fraværsdag: Boolean
    )

    fun lagVedtakshendelse() = DtoVedtakshendelse(
        vedtaksid = UUID.randomUUID(),
        fødselsdato = fødselsdato,
        innvilget = innvilget,
        grunnlagsfaktor = grunnlagsfaktor,
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )

    fun lagMeldepliktshendelse() = DtoMeldepliktshendelse(
        aktivitetPerDag = aktivitetsdager.map {
            DtoAkivitetPerDag(
                dato = it.dato,
                arbeidstimer = it.arbeidstimer,
                fraværsdag = it.fraværsdag
            )
        }
    )
}