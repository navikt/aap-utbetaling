package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import java.time.LocalDate
import java.util.*

data class MottakerModellApi(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<VedtakModellApi>,
    val aktivitetstidslinje: List<MeldeperiodeModellApi>,
    val utbetalingstidslinjehistorikk: List<UtbetalingstidslinjeModellApi>,
    val oppdragshistorikk: List<OppdragModellApi>,
    val barnetillegg: List<BarnaModellApi>,
    val tilstand: String
) {
    companion object {
        fun opprettMottaker(personident: String, fødselsdato: LocalDate): MottakerModellApi =
            Mottaker(Personident(personident), Fødselsdato(fødselsdato)).toDto()
    }
}

data class MeldeperiodeModellApi(
    val dager: List<DagModellApi>
)

data class DagModellApi(
    val dato: LocalDate,
    val arbeidstimer: Double?,
    val type: String
)

data class VedtakModellApi(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)
