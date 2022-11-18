package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Paragraf_11_20_1_ledd_ModellAPI
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Paragraf_11_20_2_ledd_2_punktum_ModellAPI
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
    val årligYtelse: Paragraf_11_20_1_ledd_ModellAPI?,
    val dagsats: Paragraf_11_20_2_ledd_2_punktum_ModellAPI?,
    val høyesteÅrligYtelseMedBarnetillegg: Double?,
    val høyesteBeløpMedBarnetillegg: Double?,
    val dagsatsMedBarnetillegg: Double?,
    val beløpMedBarnetillegg: Double?,
    val beløp: Double?,
    val arbeidsprosent: Double
)
