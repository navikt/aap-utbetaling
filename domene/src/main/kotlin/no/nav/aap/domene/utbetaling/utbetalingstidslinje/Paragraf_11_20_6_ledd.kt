package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_6_leddModellApi

internal class Paragraf_11_20_6_ledd(
    private val maksFaktorAvGrunnlag: Double,
    private val grunnlag: Paragraf_11_19_3_ledd,
    private val høyesteÅrligYtelseMedBarnetillegg: Beløp
) {

    internal constructor(grunnlag: Paragraf_11_19_3_ledd) : this (
        maksFaktorAvGrunnlag = MAKS_FAKTOR_AV_GRUNNLAG,
        grunnlag = grunnlag,
        høyesteÅrligYtelseMedBarnetillegg = grunnlag * MAKS_FAKTOR_AV_GRUNNLAG
    )

    internal operator fun div(nevner: Number) = høyesteÅrligYtelseMedBarnetillegg / nevner

    internal fun toModellApi() = Paragraf_11_20_6_leddModellApi(
        maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
        grunnlag = grunnlag.toModellApi().grunnlag,
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toModellApi()
    )

    internal companion object {
        private const val MAKS_FAKTOR_AV_GRUNNLAG = 0.9

        internal fun gjenopprett(
            maksFaktorAvGrunnlag: Double,
            grunnlag: Paragraf_11_19_3_ledd,
            høyesteÅrligYtelseMedBarnetillegg: Beløp
        ) = Paragraf_11_20_6_ledd(
            maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
            grunnlag = grunnlag,
            høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
        )
    }

}
