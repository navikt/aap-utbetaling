package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.*
import no.nav.aap.domene.utbetaling.A
import no.nav.aap.domene.utbetaling.H
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.resetSeed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MeldeperiodeTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `Beregner arbeidsprosent for arbeidsdager i perioden`() {
        val dager = 5.A(arbeidstimer = 5) + 2.H(arbeidstimer = 1) + 2.V + 3.A(arbeidstimer = 5)

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val arbeidsprosent = meldeperiode.beregnArbeidsprosent()
        assertEquals(0.56, arbeidsprosent)
    }

    @Test
    fun `Beregner total beløp for meldeperiode`() {
        val dager = 5.A(arbeidstimer = 5) + 2.H(arbeidstimer = 1) + 2.V + 3.A(arbeidstimer = 5)

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(3565.2.beløp, beløp)
    }

    @Test
    fun `100 prosent arbeid gir 0 i utbetaling`() {
        val dager = 5.A + 2.H + 5.A

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(0.beløp, beløp)
    }

    @Test
    fun `Over 60 prosent arbeid gir 0 i utbetaling`() {
        val dager = 1.A(arbeidstimer = 7.6) + 2.A + 2.A(arbeidstimer = 0) + 2.H + 3.A + 2.A(arbeidstimer = 0)

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(0.beløp, beløp)
    }

    @Test
    fun `Akkurat 60 prosent arbeid gir 40 prosent utbetaling`() {
        val dager = 3.A + 2.A(arbeidstimer = 0) + 2.H + 3.A + 2.A(arbeidstimer = 0)

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(3241.1.beløp, beløp)
    }

    @Test
    fun `Ved fravær så skal fraværsdagene ikke tas med i beregningen av arbeidsprosent`() {
        val dager = 5.F + 2.H + 5.A

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val arbeidsprosent = meldeperiode.beregnArbeidsprosent()
        assertEquals(1.0, arbeidsprosent)
    }

    @Test
    fun `Ved en fraværsdag så skal fraværsdagen tas med i beregningen av arbeidsprosent`() {
        val dager = 1.F + 4.V + 2.H + 5.A

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val arbeidsprosent = meldeperiode.beregnArbeidsprosent()
        assertEquals(0.5, arbeidsprosent)
    }

    @Test
    fun `En fraværsdag er greit ihht lovverk og beregnes på samme måte som en ventedag`() {
        val dager = 5.A(arbeidstimer = 5) + 2.H(arbeidstimer = 1) + 1.V + 1.F + 3.A(arbeidstimer = 5)

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(3565.2.beløp, beløp)
    }

    @Test
    fun `To fraværsdager fører til at disse dagene ikke regnes med i normalarbeidstid og derfor gir for høy arbeidsprosent i denne testen`() {
        val dager = 5.A(arbeidstimer = 5) + 2.H(arbeidstimer = 1) + 2.F + 3.A(arbeidstimer = 5)

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(0.beløp, beløp)
    }

    @Test
    fun `Ser bort ifra fraværsdagene i beregningen og perioden har derfor normalarbeidstid på 60 timer`() {
        val dager = 5.A(arbeidstimer = 5) + 2.H + 2.F + 1.A(arbeidstimer = 5) + 2.V

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(3241.12.beløp, beløp)
    }

    @Test
    fun `Hvis du jobber mer enn 100 prosent vil du få 0 i utbetaling`() {
        val dager = 5.F + 2.H(arbeidstimer = 1) + 5.A

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(0.beløp, beløp)
    }
}