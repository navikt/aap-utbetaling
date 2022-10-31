package no.nav.aap.app.simulering

import java.time.LocalDate

data class SimuleringResponse(
    val utbetalingstidslinje: List<TidslinjeDag>
) {
    data class TidslinjeDag(
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
        val arbeidsprosent: Double
    )
}

