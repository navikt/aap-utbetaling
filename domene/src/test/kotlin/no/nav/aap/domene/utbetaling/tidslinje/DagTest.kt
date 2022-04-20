package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.*
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Companion.summerArbeidstimer
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Companion.summerNormalArbeidstimer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DagTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.arbeidstimer)
        dag.accept(visitor)
        assertEquals(810.beløp, visitor.dagbeløp)
    }

    @Test
    fun `På helg er dagsats 0`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Helg(2 januar 2022, 0.arbeidstimer)
        dag.accept(visitor)
        assertEquals(0.beløp, visitor.dagbeløp)
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats - dagsats økes med barnetillegg`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(5), 27.beløp * 13, 0.arbeidstimer)
        dag.accept(visitor)
        assertEquals((1350 + 27 * 13).beløp, visitor.dagbeløp)
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(2), 27.beløp * 14, 0.arbeidstimer)
        dag.accept(visitor)
        assertEquals(737.beløp, visitor.dagbeløp)
    }

    @Test
    fun `En arbeidsdag har oversikt over antall timer jobbet`() {
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(5.arbeidstimer, summerteArbeidstimer)
    }

    @Test
    fun `En helgedag har oversikt over antall timer jobbet`() {
        val dag = Dag.Helg(3 januar 2022, 5.arbeidstimer)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(5.arbeidstimer, summerteArbeidstimer)
    }

    @Test
    fun `En fraværdag har ingen arbeidstimer`() {
        val dag = Dag.Fraværsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(0.arbeidstimer, summerteArbeidstimer)
    }

    @Test
    fun `En ventedag har ingen arbeidstimer`() {
        val dag = Dag.Ventedag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(0.arbeidstimer, summerteArbeidstimer)
    }

    @Test
    fun `Summer arbeidstimer over en periode`() {
        val summerteArbeidstimer = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Helg(8 januar 2022, 1.arbeidstimer),
            Dag.Helg(9 januar 2022, 1.arbeidstimer),
            Dag.Ventedag(10 januar 2022, Grunnlagsfaktor(3), 0.beløp),
            Dag.Ventedag(11 januar 2022, Grunnlagsfaktor(3), 0.beløp),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.arbeidstimer)
        ).summerArbeidstimer()
        assertEquals(42.arbeidstimer, summerteArbeidstimer)
    }

    @Test
    fun `En arbeidsdag bidrar med 7,5 timer til summen av normalarbeidstid`() {
        assertEquals(7.5.arbeidstimer, 1.A.summerNormalArbeidstimer())
    }

    @Test
    fun `To arbeidsdager bidrar med 15 timer til summen av normalarbeidstid`() {
        assertEquals(15.arbeidstimer, 2.A.summerNormalArbeidstimer())
    }

    @Test
    fun `En helgedag bidrar med 0 timer til summen av normalarbeidstid`() {
        assertEquals(0.arbeidstimer, 1.H.summerNormalArbeidstimer())
    }

    @Test
    fun `En ventedag bidrar med 7,5 timer til summen av normalarbeidstid`() {
        assertEquals(7.5.arbeidstimer, 1.V.summerNormalArbeidstimer())
    }

    @Test
    fun `En ikke-ignorert fraværsdag bidrar med 7,5 timer til summen av normalarbeidstid`() {
        assertEquals(7.5.arbeidstimer, 1.F.summerNormalArbeidstimer())
    }

    @Test
    fun `En ignorert fraværsdag bidrar med 0 timer til summen av normalarbeidstid`() {
        val dag = 1.F
        dag.first().ignoreMe()
        assertEquals(0.arbeidstimer, dag.summerNormalArbeidstimer())
    }

    private class TestDagVisitor : DagVisitor {
        lateinit var dagbeløp: Beløp

        override fun visitArbeidsdag(dagbeløp: Beløp) {
            this.dagbeløp = dagbeløp
        }

        override fun visitHelgedag(helgedag: Dag.Helg) {
            this.dagbeløp = helgedag.beløp(0.0)
        }
    }
}
