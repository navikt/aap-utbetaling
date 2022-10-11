package no.nav.aap.domene.utbetaling.entitet

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp.Element.Companion.grunnlagINOK
import java.time.LocalDate

internal object Grunnbeløp {
    private val grunnbeløp = listOf(
        Element(2022, 5, 1, 111477, 109784),
        Element(2021, 5, 1, 106399, 104716),
        Element(2020, 5, 1, 101351, 100853),
        Element(2019, 5, 1, 99858, 98866),
        Element(2018, 5, 1, 96883, 95800),
        Element(2017, 5, 1, 93634, 93281),
        Element(2016, 5, 1, 92576, 91740),
        Element(2015, 5, 1, 90068, 89502),
        Element(2014, 5, 1, 88370, 87328),
        Element(2013, 5, 1, 85245, 84204),
        Element(2012, 5, 1, 82122, 81153),
        Element(2011, 5, 1, 79216, 78024),
        Element(2010, 5, 1, 75641, 74721),
        Element(2009, 5, 1, 72881, 72006),
        Element(2008, 5, 1, 70256, 69108),
        Element(2007, 5, 1, 66812, 65505),
        Element(2006, 5, 1, 62892, 62161),
        Element(2005, 5, 1, 60699, 60059),
        Element(2004, 5, 1, 58778, 58139),
        Element(2003, 5, 1, 56861, 55964),
        Element(2002, 5, 1, 54170, 53233),
        Element(2001, 5, 1, 51360, 50603),
        Element(2000, 5, 1, 49090, 48377),
        Element(1999, 5, 1, 46950, 46423),
        Element(1998, 5, 1, 45370, 44413),
        Element(1997, 5, 1, 42500, 42000),
        Element(1996, 5, 1, 41000, 40410),
        Element(1995, 5, 1, 39230, 38847)
    )

    private class Element(
        år: Int,
        måned: Int,
        dag: Int,
        beløp: Number,
        gjennomsnittBeløp: Number
    ) {
        private val dato: LocalDate = LocalDate.of(år, måned, dag)
        private val beløp: Beløp = beløp.beløp
        private val gjennomsnittBeløp: Beløp = gjennomsnittBeløp.beløp

        companion object {
             fun Iterable<Element>.grunnlagINOK(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) =
                grunnlagsfaktor * finnGrunnbeløpForDato(dato).beløp

            private fun Iterable<Element>.finnGrunnbeløpForDato(dato: LocalDate) = this
                .sortedByDescending { it.dato }
                .first { dato >= it.dato }
        }
    }

    internal fun grunnlagINOK(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor): Beløp =
        grunnbeløp.grunnlagINOK(dato, grunnlagsfaktor)
}
