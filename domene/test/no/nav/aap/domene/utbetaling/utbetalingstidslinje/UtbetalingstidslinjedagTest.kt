package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.Utbetalingsdager.I
import no.nav.aap.domene.utbetaling.Utbetalingsdager.U
import no.nav.aap.domene.utbetaling.Utbetalingsdager.resetSeed
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.januar
import no.nav.aap.domene.utbetaling.visitor.UtbetalingsdagVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UtbetalingstidslinjedagTest {

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
        val dager = 1.U(
            grunnlagsfaktor = 5,
            arbeidsprosent = 0,
            barn = (1..13).map { Barnetillegg.Barn(Fødselsdato(3 januar 2017)) })
        dager.forEach { it.accept(visitor) }
        assertEquals(1701.45.beløp, visitor.dagbeløp)
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val visitor = TestDagVisitor()
        val dager = 1.U(
            grunnlagsfaktor = 2.1,
            fødselsdato = 1 januar 2004,
            arbeidsprosent = 0,
            barn = (1..14).map { Barnetillegg.Barn(Fødselsdato(3 januar 2017)) })
        dager.forEach { it.accept(visitor) }
        assertEquals(773.44.beløp, visitor.dagbeløp)
    }

    private class TestDagVisitor : UtbetalingsdagVisitor {
        lateinit var dagbeløp: Beløp

        override fun visitUtbetaling(dag: Utbetalingstidslinjedag.Utbetalingsdag, dato: LocalDate, beløp: Beløp) {
            this.dagbeløp = beløp
        }

        override fun visitIkkeUtbetaling(dag: Utbetalingstidslinjedag.IkkeUtbetalingsdag, dato: LocalDate) {
            this.dagbeløp = 0.beløp
        }
    }
}
