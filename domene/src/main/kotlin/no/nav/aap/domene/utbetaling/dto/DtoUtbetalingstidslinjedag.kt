package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate

data class DtoUtbetalingstidslinje(
    val dager: List<DtoUtbetalingstidslinjedag>
)

data class DtoUtbetalingstidslinjedag(
    val dato: LocalDate,
    val grunnlagsfaktor: Double?,
    val barnetillegg: Double?,
    val grunnlag: Double?,
    val dagsats: Double?,
    val høyestebeløpMedBarnetillegg: Double?,
    val beløpMedBarnetillegg: Double?,
    val beløp: Double?,
    val arbeidsprosent: Double
)
