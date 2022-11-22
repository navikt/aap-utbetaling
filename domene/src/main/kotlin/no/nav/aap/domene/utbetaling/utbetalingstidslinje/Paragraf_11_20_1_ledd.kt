package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_1_ledd_ModellApi

internal class Paragraf_11_20_1_ledd private constructor(
    private val faktorForReduksjonAvGrunnlag: Double,
    private val inntektsgrunnlag: Paragraf_11_19_3_ledd,
    private val årligYtelse: Beløp
) {

    internal constructor(grunnlag: Paragraf_11_19_3_ledd) : this(
        faktorForReduksjonAvGrunnlag = FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG,
        inntektsgrunnlag = grunnlag,
        årligYtelse = grunnlag * FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG
    )

    internal operator fun div(nevner: Number) = årligYtelse / nevner

    internal fun toModellApi() = Paragraf_11_20_1_ledd_ModellApi(
        faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
        inntektsgrunnlag = inntektsgrunnlag.toModellApi().grunnlag,
        årligytelse = årligYtelse.toModellApi(),
    )

    internal companion object {
        private const val FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG = 0.66

        internal fun gjenopprett(
            faktorForReduksjonAvGrunnlag: Double,
            inntektsgrunnlag: Paragraf_11_19_3_ledd,
            årligYtelse: Beløp
        ) = Paragraf_11_20_1_ledd(
            faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
            inntektsgrunnlag = inntektsgrunnlag,
            årligYtelse = årligYtelse
        )
    }
}
