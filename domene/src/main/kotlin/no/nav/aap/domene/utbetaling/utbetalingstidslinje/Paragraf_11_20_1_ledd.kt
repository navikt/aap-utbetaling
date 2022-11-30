package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_1_ledd_ModellApi

internal class Paragraf_11_20_1_ledd private constructor(
    private val faktorForReduksjonAvGrunnlag: Double,
    private val grunnlag: Beløp,
    private val årligYtelse: Beløp
) {

    internal constructor(grunnlag: Beløp) : this(
        faktorForReduksjonAvGrunnlag = FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG,
        grunnlag = grunnlag,
        årligYtelse = grunnlag * FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG
    )

    internal operator fun div(nevner: Number) = årligYtelse / nevner

    internal fun toModellApi() = Paragraf_11_20_1_ledd_ModellApi(
        faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
        grunnlag = grunnlag.toModellApi(),
        årligytelse = årligYtelse.toModellApi(),
    )

    internal companion object {
        private const val FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG = 0.66

        internal fun gjenopprett(
            faktorForReduksjonAvGrunnlag: Double,
            grunnlag: Beløp,
            årligYtelse: Beløp,
        ) = Paragraf_11_20_1_ledd(
            faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
            grunnlag = grunnlag,
            årligYtelse = årligYtelse,
        )
    }
}
