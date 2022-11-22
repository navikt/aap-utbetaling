package no.nav.aap.domene.utbetaling.modellapi

import java.time.LocalDate

data class UtbetalingstidslinjeModellApi(
    val dager: List<UtbetalingstidslinjedagModellApi>
)

data class UtbetalingstidslinjedagModellApi(
    val type: String,
    val dato: LocalDate,
    val grunnlagsfaktor: Double?,
    val barnetillegg: Double?,
    val grunnlag: Paragraf_11_19_3_leddModellApi?,
    val årligYtelse: Paragraf_11_20_1_ledd_ModellApi?,
    val dagsats: Paragraf_11_20_2_ledd_2_punktum_ModellApi?,
    val høyesteÅrligYtelseMedBarnetillegg: Paragraf_11_20_6_leddModellApi?,
    val høyesteBeløpMedBarnetillegg: Double?,
    val dagsatsMedBarnetillegg: Double?,
    val beløpMedBarnetillegg: Double?,
    val beløp: Double?,
    val arbeidsprosent: Double
)
