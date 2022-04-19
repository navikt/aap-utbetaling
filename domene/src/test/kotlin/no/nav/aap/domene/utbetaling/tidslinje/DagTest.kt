package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.januar
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Companion.summerArbeidstimer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DagTest {

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0)
        dag.accept(visitor)
        assertEquals(810.beløp, visitor.dagsats)
    }

    @Test
    fun `På helg er dagsats 0`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(2 januar 2022, Grunnlagsfaktor(4), 0.beløp, 0.0)
        dag.accept(visitor)
        assertEquals(0.beløp, visitor.dagsats)
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats - dagsats økes med barnetillegg`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(5), 27.beløp * 13, 0.0)
        dag.accept(visitor)
        assertEquals((1350 + 27 * 13).beløp, visitor.dagsats)
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val visitor = TestDagVisitor()
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(2), 27.beløp * 14, 0.0)
        dag.accept(visitor)
        assertEquals(737.beløp, visitor.dagsats)
    }

    @Test
    fun `En arbeidsdag har oversikt over antall timer jobbet`() {
        val dag = Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(5.0, summerteArbeidstimer)
    }

    @Test
    fun `En helgedag har oversikt over antall timer jobbet`() {
        val dag = Dag.Helg(3 januar 2022, 5.0)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(5.0, summerteArbeidstimer)
    }

    @Test
    fun `En fraværdag har ingen arbeidstimer`() {
        val dag = Dag.Fraværsdag(3 januar 2022)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(0.0, summerteArbeidstimer)
    }

    @Test
    fun `En ventedag har ingen arbeidstimer`() {
        val dag = Dag.Ventedag(3 januar 2022)
        val summerteArbeidstimer = listOf(dag).summerArbeidstimer()
        assertEquals(0.0, summerteArbeidstimer)
    }

    @Test
    fun `Summer arbeidstimer over en periode`() {
        val summerteArbeidstimer = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Helg(8 januar 2022,1.0),
            Dag.Helg(9 januar 2022,1.0),
            Dag.Ventedag(10 januar 2022),
            Dag.Ventedag(11 januar 2022),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0)
        ).summerArbeidstimer()
        assertEquals(42.0, summerteArbeidstimer)
    }

    private class TestDagVisitor : DagVisitor {
        lateinit var dagsats: Beløp

        override fun visitArbeidsdag(dagsats: Beløp) {
            this.dagsats = dagsats
        }
    }
}
