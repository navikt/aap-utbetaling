package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Aktivitetsdager.A
import no.nav.aap.domene.utbetaling.Aktivitetsdager.F
import no.nav.aap.domene.utbetaling.Aktivitetsdager.H
import no.nav.aap.domene.utbetaling.Aktivitetsdager.resetSeed
import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Endringskode
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.inspektør
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.OppdragBuilder
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FraAktivitetstidslinjeTilOppdragBuilderTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `10 sammenhengende arbeidsdager pluss helg gir 10 stønadsdager i oppdraget`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 5.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder(Grunnlagsfaktor(3))
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        utbetalingstidslinje.barnetillegg(Barnetillegg(emptyList()))

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(10, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
    }

    @Test
    fun `Utbetaler på fraværsdag så lenge det bare er en i perioden`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 1.F + 4.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder(Grunnlagsfaktor(3))
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        utbetalingstidslinje.barnetillegg(Barnetillegg(emptyList()))

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(10, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
    }

    @Test
    fun `Utbetaler ikke på fraværsdager når det er to sammenhengende fraværsdager i perioden`() {
        val dager = 5.A(arbeidstimer = 0) + 2.H + 2.F + 3.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder(Grunnlagsfaktor(3))
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        utbetalingstidslinje.barnetillegg(Barnetillegg(emptyList()))

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(8, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(2, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
    }

    @Test
    fun `Utbetaler ikke på fraværsdager når det er to usammenhengende fraværsdager i perioden`() {
        val dager = 1.F + 4.A(arbeidstimer = 0) + 2.H + 1.F + 4.A(arbeidstimer = 0) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder(Grunnlagsfaktor(3))
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        utbetalingstidslinje.barnetillegg(Barnetillegg(emptyList()))

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(8, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(2, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertNull(inspektør.datoStatusFom(1))
        assertEquals(Endringskode.NY, inspektør.endringskode)
    }

    @Test
    fun `Bare fraværsdager i perioden gir ingen stønadsdager`() {
        val dager = 5.F + 2.H + 5.F + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder(Grunnlagsfaktor(3))
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(0, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(0, inspektør.antallLinjer())
        assertEquals(Endringskode.NY, inspektør.endringskode)
    }

    @Test
    fun `Bare arbeidsdager med 7,5 timer arbeid i perioden gir ingen stønadsdager`() {
        val dager = 5.A(arbeidstimer = 7.5) + 2.H + 5.A(arbeidstimer = 7.5) + 2.H
        val aktivitetstidslinje = Aktivitetstidslinje(listOf(Meldeperiode(dager)))

        val utbetalingstidslinjeBuilder = UtbetalingstidslinjeBuilder(Grunnlagsfaktor(3))
        val utbetalingstidslinje: Utbetalingstidslinje = utbetalingstidslinjeBuilder.build(aktivitetstidslinje)

        val oppdragBuilder = OppdragBuilder()
        val oppdrag: Oppdrag = oppdragBuilder.build(utbetalingstidslinje)

        assertEquals(0, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(0, inspektør.antallLinjer())
        assertEquals(Endringskode.NY, inspektør.endringskode)
    }
}
