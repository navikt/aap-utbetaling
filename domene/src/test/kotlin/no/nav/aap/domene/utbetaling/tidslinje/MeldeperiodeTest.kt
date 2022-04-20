package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.*
import no.nav.aap.domene.utbetaling.A
import no.nav.aap.domene.utbetaling.H
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.resetSeed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

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

    }
}
