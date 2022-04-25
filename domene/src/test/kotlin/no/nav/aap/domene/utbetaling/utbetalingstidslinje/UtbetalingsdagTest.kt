package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Utbetalingsdager.I
import no.nav.aap.domene.utbetaling.Utbetalingsdager.U
import no.nav.aap.domene.utbetaling.Utbetalingsdager.resetSeed
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UtbetalingsdagTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats`() {
        val visitor = TestDagVisitor()
        val dager = 1.U(arbeidsprosent = 0)
        dager.forEach { it.accept(visitor) }
        assertEquals(810.27.beløp, visitor.dagbeløp)
    }

    @Test
    fun `På en ikke-utbetalingsdag er dagsats 0`() {
        val visitor = TestDagVisitor()
        val dager = 1.I
        dager.forEach { it.accept(visitor) }
        assertEquals(0.beløp, visitor.dagbeløp)
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats - dagsats økes med barnetillegg`() {
        val visitor = TestDagVisitor()
        val dager = 1.U(grunnlagsfaktor = 5, arbeidsprosent = 0, barnetillegg = 27 * 13)
        dager.forEach { it.accept(visitor) }
        assertEquals(1701.45.beløp, visitor.dagbeløp)
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val visitor = TestDagVisitor()
        val dager = 1.U(grunnlagsfaktor = 2, arbeidsprosent = 0, barnetillegg = 27 * 14)
        dager.forEach { it.accept(visitor) }
        assertEquals(736.61.beløp, visitor.dagbeløp)
    }

    private class TestDagVisitor : UtbetalingsdagVisitor {
        lateinit var dagbeløp: Beløp

        override fun visitUtbetaling(dag: Utbetalingsdag.Utbetaling, dato: LocalDate, beløp: Beløp) {
            this.dagbeløp = beløp
        }

        override fun visitIkkeUtbetaling(dag: Utbetalingsdag.IkkeUtbetaling, dato: LocalDate) {
            this.dagbeløp = 0.beløp
        }
    }
}
