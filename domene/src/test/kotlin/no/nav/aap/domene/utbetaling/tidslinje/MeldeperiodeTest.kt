package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.januar
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

    private var seed = 3 januar 2022
        get() {
            val f = field
            field = field.plusDays(1)
            return f
        }

    private fun resetSeed(dato: LocalDate = 3 januar 2022) {
        seed = dato
    }

    private val Int.A get() = A()
    private fun Int.A(grunnlagsfaktor: Number = 3, barnetillegg: Number = 0, arbeidstimer: Number = 7.5) = (1..this)
        .map { Dag.Arbeidsdag(seed, Grunnlagsfaktor(grunnlagsfaktor), barnetillegg.beløp, arbeidstimer.toDouble()) }

    private val Int.H get() = H()
    private fun Int.H(arbeidstimer: Number = 0) = (1..this)
        .map { Dag.Helg(seed, arbeidstimer.toDouble()) }

    private val Int.V get() = V()
    private fun Int.V(grunnlagsfaktor: Number = 3, barnetillegg: Number = 0) = (1..this)
        .map { Dag.Ventedag(seed, Grunnlagsfaktor(grunnlagsfaktor), barnetillegg.beløp) }
}
