package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate
import java.util.*

data class DtoMottaker(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<DtoVedtak>,
    val aktivitetstidslinje: List<DtoMeldeperiode>,
    val tilstand: String
)

data class DtoMeldeperiode(
    val dager: List<DtoDag>
)

data class DtoDag(
    val dato: LocalDate,
    val arbeidstimer: Double?,
    val type: String
)

data class DtoVedtak (
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)

