package no.nav.aap.domene.utbetaling.entitet

import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp.Element.Companion.årligYtelseINOK
import java.time.LocalDate

internal object Grunnbeløp {
    private val grunnbeløp = listOf(
        Element(LocalDate.of(2021, 5, 1), Beløp(106399.0), Beløp(104716.0)),
        Element(LocalDate.of(2020, 5, 1), Beløp(101351.0), Beløp(100853.0)),
        Element(LocalDate.of(2019, 5, 1), Beløp(99858.0), Beløp(98866.0)),
        Element(LocalDate.of(2018, 5, 1), Beløp(96883.0), Beløp(95800.0)),
        Element(LocalDate.of(2017, 5, 1), Beløp(93634.0), Beløp(93281.0)),
        Element(LocalDate.of(2016, 5, 1), Beløp(92576.0), Beløp(91740.0)),
        Element(LocalDate.of(2015, 5, 1), Beløp(90068.0), Beløp(89502.0)),
        Element(LocalDate.of(2014, 5, 1), Beløp(88370.0), Beløp(87328.0))
    )

    private class Element(
        private val dato: LocalDate,
        private val beløp: Beløp,
        private val gjennomsnittBeløp: Beløp
    ) {
        companion object {
            fun Iterable<Element>.årligYtelseINOK(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) =
                grunnlagsfaktor * finnGrunnbeløpForDato(dato).beløp

            private fun Iterable<Element>.finnGrunnbeløpForDato(dato: LocalDate) = this
                .sortedByDescending { it.dato }
                .first { dato >= it.dato }
        }
    }

    internal fun årligYtelseINOK(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor): Beløp =
        grunnbeløp.årligYtelseINOK(dato, grunnlagsfaktor)
}
