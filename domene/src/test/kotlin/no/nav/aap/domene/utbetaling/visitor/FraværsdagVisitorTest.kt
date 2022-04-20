package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.F
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.resetSeed
import no.nav.aap.domene.utbetaling.tidslinje.FraværsdagVisitor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FraværsdagVisitorTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `Dersom vi har kun en fraværsdag blir beløpet summert til angitt`() {
        val dag = 1.F

        val visitor = FraværsdagVisitor()

        dag.forEach { it.accept(visitor) }

        assertEquals(810.27.beløp, dag.first().beløp())
        assertEquals(810.27.beløp, dag.first().beløp(0.0))
    }

    @Test
    fun `Dersom vi har to fraværsdager blir beløpet summert til 0`() {
        val dag = 2.F

        val visitor = FraværsdagVisitor()

        dag.forEach { it.accept(visitor) }

        assertEquals(0.beløp, dag.first().beløp())
        assertEquals(0.beløp, dag.last().beløp())

        assertEquals(0.beløp, dag.first().beløp(0.5))
        assertEquals(0.beløp, dag.last().beløp(0.5))
    }

    @Test
    fun `Dersom vi har tre fraværsdager blir beløpet summert til 0`() {
        val dag = 3.F

        val visitor = FraværsdagVisitor()

        dag.forEach { it.accept(visitor) }

        assertEquals(0.beløp, dag[0].beløp())
        assertEquals(0.beløp, dag[1].beløp())
        assertEquals(0.beløp, dag[2].beløp())

        assertEquals(0.beløp, dag[0].beløp(0.5))
        assertEquals(0.beløp, dag[1].beløp(0.5))
        assertEquals(0.beløp, dag[2].beløp(0.5))
    }

}