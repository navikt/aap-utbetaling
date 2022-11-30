package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_6_leddModellApi

internal class Paragraf_11_20_6_ledd(
    private val maksFaktorAvGrunnlag: Double,
    private val grunnlag: Beløp,
    private val høyesteÅrligYtelseMedBarnetillegg: Beløp
) {

    internal constructor(grunnlag: Beløp) : this(
        maksFaktorAvGrunnlag = MAKS_FAKTOR_AV_GRUNNLAG,
        grunnlag = grunnlag,
        høyesteÅrligYtelseMedBarnetillegg = grunnlag * MAKS_FAKTOR_AV_GRUNNLAG
    )

    internal operator fun div(nevner: Number) = høyesteÅrligYtelseMedBarnetillegg / nevner

    internal fun høyesteBeløpMedBarnetillegg() = Paragraf_11_20_2_ledd_2_punktum(høyesteÅrligYtelseMedBarnetillegg)

    internal fun toModellApi() = Paragraf_11_20_6_leddModellApi(
        maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
        grunnlag = grunnlag.toModellApi(),
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toModellApi()
    )

    internal companion object {
        private const val MAKS_FAKTOR_AV_GRUNNLAG = 0.9

        internal fun gjenopprett(
            maksFaktorAvGrunnlag: Double,
            grunnlag: Beløp,
            høyesteÅrligYtelseMedBarnetillegg: Beløp
        ) = Paragraf_11_20_6_ledd(
            maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
            grunnlag = grunnlag,
            høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
        )
    }
}
