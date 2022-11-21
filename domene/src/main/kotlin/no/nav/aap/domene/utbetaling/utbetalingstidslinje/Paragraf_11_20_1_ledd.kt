package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp

internal class Paragraf_11_20_1_ledd private constructor(
    private val faktorForReduksjonAvGrunnlag: Double,
    private val inntektsgrunnlag: Beløp,
    private val årligYtelse: Beløp
) {

    internal constructor(inntektsgrunnlag: Beløp) : this(
        faktorForReduksjonAvGrunnlag = FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG,
        inntektsgrunnlag = inntektsgrunnlag,
        årligYtelse = inntektsgrunnlag * FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG
    )

    internal operator fun div(nevner: Number) = årligYtelse / nevner

    internal fun toDto() = Paragraf_11_20_1_ledd_ModellAPI(
        faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
        inntektsgrunnlag = inntektsgrunnlag.toDto(),
        årligytelse = årligYtelse.toDto(),
    )

    internal companion object {
        private const val FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG = 0.66

        internal fun gjenopprett(
            faktorForReduksjonAvGrunnlag: Double,
            inntektsgrunnlag: Beløp,
            årligYtelse: Beløp
        ) = Paragraf_11_20_1_ledd(
            faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
            inntektsgrunnlag = inntektsgrunnlag,
            årligYtelse = årligYtelse
        )
    }
}
