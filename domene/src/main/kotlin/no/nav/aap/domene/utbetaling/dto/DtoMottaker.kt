package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import java.time.LocalDate
import java.util.*

data class DtoMottaker(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<DtoVedtak>,
    val aktivitetstidslinje: List<DtoMeldeperiode>,
    val utbetalingstidslinjehistorikk: List<DtoUtbetalingstidslinje>,
    val oppdragshistorikk: List<DtoOppdrag>,
    val tilstand: String
) {
    companion object {
        fun opprettMottaker(personident: String, fødselsdato: LocalDate): DtoMottaker =
            Mottaker(Personident(personident), Fødselsdato(fødselsdato)).toDto()
    }
}

data class DtoMeldeperiode(
    val dager: List<DtoDag>
)

data class DtoDag(
    val dato: LocalDate,
    val arbeidstimer: Double?,
    val type: String
)

data class DtoVedtak(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)
