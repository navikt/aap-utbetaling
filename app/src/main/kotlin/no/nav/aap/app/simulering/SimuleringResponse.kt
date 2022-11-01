package no.nav.aap.app.simulering

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
}
