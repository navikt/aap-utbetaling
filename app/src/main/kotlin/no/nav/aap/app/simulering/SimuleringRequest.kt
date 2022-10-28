package no.nav.aap.app.simulering

import java.time.LocalDate

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
}