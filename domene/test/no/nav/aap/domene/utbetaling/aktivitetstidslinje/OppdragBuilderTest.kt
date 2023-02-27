package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Utbetalingsdager.I
import no.nav.aap.domene.utbetaling.Utbetalingsdager.S
import no.nav.aap.domene.utbetaling.Utbetalingsdager.U
import no.nav.aap.domene.utbetaling.Utbetalingsdager.resetSeed
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Endringskode
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.inspektør
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.OppdragBuilder
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class OppdragBuilderTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `10 sammenhengende utbetalingsdager gir 10 stønadsdager i oppdraget`() {
        val dager = 5.U(arbeidsprosent = 0) + 2.S + 5.U(arbeidsprosent = 0) + 2.S
        val utbetalingstidslinje = Utbetalingstidslinje(dager)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(10, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(8100, inspektør.totalBeløp(0))
    }

    @Test
    fun `Ikke-utbetalte dager mellom to utbetalingsperioder gir to utbetalingslinjer i oppdraget`() {
        val dager = 5.U(arbeidsprosent = 0) + 2.S + 2.I + 3.U(arbeidsprosent = 0) + 2.S
        val utbetalingstidslinje = Utbetalingstidslinje(dager)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(8, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(2, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(6480, inspektør.totalBeløp(0))
    }

    @Test
    fun `Ikke-utbetalte dager før og i mellom to utbetalingsperioder gir to utbetalingslinjer i oppdraget`() {
        val dager = 1.I + 4.U(arbeidsprosent = 0) + 2.S + 1.I + 4.U(arbeidsprosent = 0) + 2.S
        val utbetalingstidslinje = Utbetalingstidslinje(dager)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(8, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(2, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertNull(inspektør.datoStatusFom(1))
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(6480, inspektør.totalBeløp(0))
    }

    @Test
    fun `Bare ikke-utbetalingsdager i perioden gir ingen stønadsdager`() {
        val dager = 5.I + 2.S + 5.I + 2.S
        val utbetalingstidslinje = Utbetalingstidslinje(dager)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(0, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(0, inspektør.antallLinjer())
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(0, inspektør.totalBeløp(0))
    }

    @Test
    fun `10 sammenhengende utbetalingsdager med 50 prosent arbeid gir 10 stønadsdager i oppdraget`() {
        val dager = 5.U(arbeidsprosent = 0.5) + 2.S + 5.U(arbeidsprosent = 0.5) + 2.S
        val utbetalingstidslinje = Utbetalingstidslinje(dager)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(10, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(4050, inspektør.totalBeløp(0))
    }
}
