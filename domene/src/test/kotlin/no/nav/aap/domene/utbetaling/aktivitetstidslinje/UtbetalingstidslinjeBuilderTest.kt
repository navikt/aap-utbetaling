package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Aktivitetsdager.A
import no.nav.aap.domene.utbetaling.Aktivitetsdager.H
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingsdag
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.UtbetalingstidslinjeVisitor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class UtbetalingstidslinjeBuilderTest {

    @Test
    fun `10 sammenhengende arbeidsdager pluss helg gir 10 stønadsdager i oppdraget`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 5.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(10, inspektør.antallUtbetalingsdager)
        assertEquals(0, inspektør.antallIkkeUtbetalingsdager)
    }

    private val Utbetalingstidslinje.inspektør
        get() = UtbetalingstidslinjeInspektør().also { accept(it) }

    private class UtbetalingstidslinjeInspektør : UtbetalingstidslinjeVisitor {
        var antallUtbetalingsdager: Int = 0
        var antallIkkeUtbetalingsdager: Int = 0

        override fun visitUtbetaling(dag: Utbetalingsdag.Utbetaling, dato: LocalDate, beløp: Beløp) {
            antallUtbetalingsdager++
        }

        override fun visitIkkeUtbetaling(dag: Utbetalingsdag.IkkeUtbetaling, dato: LocalDate) {
            antallIkkeUtbetalingsdager++
        }
    }
}
