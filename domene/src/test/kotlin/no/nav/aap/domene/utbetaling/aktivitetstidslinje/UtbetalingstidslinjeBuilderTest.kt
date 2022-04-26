package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Aktivitetsdager.A
import no.nav.aap.domene.utbetaling.Aktivitetsdager.F
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
    fun `10 sammenhengende arbeidsdager pluss helg gir 10 stønadsdager`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 5.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(10, inspektør.antallUtbetalingsdager)
        assertEquals(0, inspektør.antallIkkeUtbetalingsdager)
    }

    @Test
    fun `10 sammenhengende arbeidsdager pluss helg med fullt arbeid gir 0 stønadsdager`() {
        val dager = 5.A(arbeidstimer = 7.5) + 2.H + 5.A(arbeidstimer = 7.5) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(0, inspektør.antallUtbetalingsdager)
        assertEquals(10, inspektør.antallIkkeUtbetalingsdager)
    }

    @Test
    fun `5 arbeidsdager, 1 fraværsdag og 4 arbeidsdager gir 10 stønadsdager`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 1.F + 4.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(10, inspektør.antallUtbetalingsdager)
        assertEquals(0, inspektør.antallIkkeUtbetalingsdager)
    }

    @Test
    fun `5 arbeidsdager, 2 fraværsdager og 3 arbeidsdager gir 8 stønadsdager`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 2.F + 3.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(8, inspektør.antallUtbetalingsdager)
        assertEquals(2, inspektør.antallIkkeUtbetalingsdager)
    }

    @Test
    fun `Utbetaler ikke på fraværsdager når det er to usammenhengende fraværsdager i perioden`() {
        val dager = 1.F + 4.A(arbeidstimer = 0) + 2.H + 1.F + 4.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(8, inspektør.antallUtbetalingsdager)
        assertEquals(2, inspektør.antallIkkeUtbetalingsdager)
    }

    @Test
    fun `Bare fraværsdager i perioden gir ingen stønadsdager`() {
        val dager = 5.F + 2.H + 5.F + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder()
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val inspektør = utbetalingstidslinje.inspektør

        assertEquals(0, inspektør.antallUtbetalingsdager)
        assertEquals(10, inspektør.antallIkkeUtbetalingsdager)
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
