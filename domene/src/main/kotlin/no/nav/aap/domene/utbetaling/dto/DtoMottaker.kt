package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate
import java.util.*

data class DtoMottaker(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<DtoVedtak>
)

data class DtoVedtak (
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)

