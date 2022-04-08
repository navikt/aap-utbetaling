package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.januar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class DagTest{

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats`() {
        val visitor = TestDagVisitor()
        val dag = Dag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp)
        dag.accept(visitor)
        assertEquals(810.beløp, visitor.dagsats)
    }

    @Test
    fun `På helg er dagsats 0`() {
        val visitor = TestDagVisitor()
        val dag = Dag(2 januar 2022, Grunnlagsfaktor(4), 0.beløp)
        dag.accept(visitor)
        assertEquals(0.beløp, visitor.dagsats)
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats - dagsats økes med barnetillegg`() {
        val visitor = TestDagVisitor()
        val dag = Dag(3 januar 2022, Grunnlagsfaktor(5), 27.beløp * 13)
        dag.accept(visitor)
        assertEquals((1350 + 27 * 13).beløp, visitor.dagsats)
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val visitor = TestDagVisitor()
        val dag = Dag(3 januar 2022, Grunnlagsfaktor(2), 27.beløp * 14)
        dag.accept(visitor)
        assertEquals(737.beløp, visitor.dagsats)
    }

    private class TestDagVisitor : DagVisitor {
        lateinit var dagsats: Beløp

        override fun visitDag(dagsats: Beløp) {
            this.dagsats = dagsats
        }
    }
}
