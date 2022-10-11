package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate

data class DtoUtbetalingstidslinje(
    val dager: List<DtoUtbetalingstidslinjedag>
)

data class DtoUtbetalingstidslinjedag(
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
