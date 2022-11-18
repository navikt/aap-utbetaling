package no.nav.aap.app.simulering

import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import java.time.LocalDate

data class SimuleringResponse(
    val aktivitetstidslinje: List<Aktivitetsdag>,
    val utbetalingstidslinje: List<Utbetalingstidslinjedag>,
    val kombinerteDager: List<KombinertDag>,
) {
    data class KombinertDag(
        val aktivitetsdag: Aktivitetsdag,
        val utbetalingstidslinjedag: Utbetalingstidslinjedag?,
    )

    data class Aktivitetsdag(
        val dato: LocalDate,
        val arbeidstimer: Double?,
        val type: String,
    )

    data class Utbetalingstidslinjedag(
        val type: String,
        val dato: LocalDate,
        val grunnlagsfaktor: Double?,
        val barnetillegg: Double?,
        val grunnlag: Double?,
        val årligYtelse: Double?,
        val dagsats: Double?,
        val høyesteÅrligYtelseMedBarnetillegg: Double?,
        val høyesteBeløpMedBarnetillegg: Double?,
        val dagsatsMedBarnetillegg: Double?,
        val beløpMedBarnetillegg: Double?,
        val beløp: Double?,
        val arbeidsprosent: Double,
    )

    companion object {
        fun lagNy(endretMottakerMedBarn: DtoMottaker): SimuleringResponse {
            val aktivitetstidslinje = endretMottakerMedBarn.aktivitetstidslinje.single().dager.map {
                Aktivitetsdag(
                    dato = it.dato,
                    arbeidstimer = it.arbeidstimer,
                    type = it.type,
                )
            }
            val utbetalingstidslinje = endretMottakerMedBarn.utbetalingstidslinjehistorikk.single().dager.map {
                Utbetalingstidslinjedag(
                    type = it.type,
                    dato = it.dato,
                    grunnlagsfaktor = it.grunnlagsfaktor,
                    barnetillegg = it.barnetillegg,
                    grunnlag = it.grunnlag,
                    årligYtelse = it.årligYtelse?.årligytelse,
                    dagsats = it.dagsats?.dagsats,
                    høyesteÅrligYtelseMedBarnetillegg = it.høyesteÅrligYtelseMedBarnetillegg,
                    høyesteBeløpMedBarnetillegg = it.høyesteBeløpMedBarnetillegg,
                    dagsatsMedBarnetillegg = it.dagsatsMedBarnetillegg,
                    beløpMedBarnetillegg = it.beløpMedBarnetillegg,
                    beløp = it.beløp,
                    arbeidsprosent = it.arbeidsprosent,
                )
            }

            val kombinerteDager = aktivitetstidslinje
                .fold(emptyList<KombinertDag>()) { acc, aktivitetsdag ->
                    val tidslinjeDag = utbetalingstidslinje.singleOrNull { it.dato == aktivitetsdag.dato }
                    acc + KombinertDag(aktivitetsdag, tidslinjeDag)
                }
            return SimuleringResponse(
                aktivitetstidslinje = aktivitetstidslinje,
                utbetalingstidslinje = utbetalingstidslinje,
                kombinerteDager = kombinerteDager,
            )
        }
    }
}
