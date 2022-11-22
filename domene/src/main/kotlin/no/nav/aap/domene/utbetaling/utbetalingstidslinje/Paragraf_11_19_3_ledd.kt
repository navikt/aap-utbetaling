package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_19_3_leddModellApi
import java.time.LocalDate

internal class Paragraf_11_19_3_ledd(
    private val dato: LocalDate,
    private val grunnlagsfaktor: Grunnlagsfaktor,
    private val grunnlag: Beløp
) {

    internal constructor(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) : this(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnlag = Grunnbeløp.grunnlagINOK(dato, grunnlagsfaktor)
    )

    internal operator fun times(faktor: Double) = grunnlag * faktor

    internal fun toModellApi() = Paragraf_11_19_3_leddModellApi(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor.toModellApi(),
        grunnlag = grunnlag.toModellApi()
    )

    internal companion object {
        internal fun gjenopprett(
            dato: LocalDate,
            grunnlagsfaktor: Grunnlagsfaktor,
            grunnlag: Beløp
        ) = Paragraf_11_19_3_ledd(
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor,
            grunnlag = grunnlag
        )
    }

}
