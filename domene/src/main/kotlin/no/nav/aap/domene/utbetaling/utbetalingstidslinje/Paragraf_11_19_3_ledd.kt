package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_19_3_leddModellApi
import java.time.LocalDate

internal class Paragraf_11_19_3_ledd private constructor(
    private val dato: LocalDate,
    private val grunnlagsfaktor: Grunnlagsfaktor,
    private val grunnbeløp: Beløp,
    private val grunnlag: Beløp
) {

    internal constructor(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) : this(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnbeløp = Grunnbeløp.finnGrunnbeløpForDato(dato),
    )

    private constructor(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor, grunnbeløp: Grunnbeløp.Element) : this(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnbeløp = grunnbeløp.grunnlagINOK(Grunnlagsfaktor(1)),
        grunnlag = grunnbeløp.grunnlagINOK(grunnlagsfaktor),
    )

    internal operator fun times(faktor: Double) = grunnlag * faktor

    internal fun toModellApi() = Paragraf_11_19_3_leddModellApi(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor.toModellApi(),
        grunnbeløp = grunnbeløp.toModellApi(),
        grunnlag = grunnlag.toModellApi()
    )

    internal companion object {
        internal fun gjenopprett(
            dato: LocalDate,
            grunnlagsfaktor: Grunnlagsfaktor,
            grunnbeløp: Beløp,
            grunnlag: Beløp
        ) = Paragraf_11_19_3_ledd(
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor,
            grunnbeløp = grunnbeløp,
            grunnlag = grunnlag
        )
    }
}
