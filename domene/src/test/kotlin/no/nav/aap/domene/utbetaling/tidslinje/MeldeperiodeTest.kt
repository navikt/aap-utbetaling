package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MeldeperiodeTest {

    @Test
    fun `Beregner arbeidsprosent for arbeidsdager i perioden`() {
        val dager = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Helg(8 januar 2022, 1.0),
            Dag.Helg(9 januar 2022, 1.0),
            Dag.Ventedag(10 januar 2022, Grunnlagsfaktor(3), 0.beløp),
            Dag.Ventedag(11 januar 2022, Grunnlagsfaktor(3), 0.beløp),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0)
        )

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val arbeidsprosent = meldeperiode.beregnArbeidsprosent()
        assertEquals(0.56, arbeidsprosent)
    }

    @Test
    fun `Beregner total beløp for meldeperiode`() {
        val dager = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Helg(8 januar 2022, 1.0),
            Dag.Helg(9 januar 2022, 1.0),
            Dag.Ventedag(10 januar 2022, Grunnlagsfaktor(3), 0.beløp),
            Dag.Ventedag(11 januar 2022, Grunnlagsfaktor(3), 0.beløp),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 5.0)
        )

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(3565.2.beløp, beløp)
    }

    @Test
    fun `100 prosent arbeid gir 0 i utbetaling`() {
        val dager = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Helg(8 januar 2022, 0.0),
            Dag.Helg(9 januar 2022, 0.0),
            Dag.Arbeidsdag(10 januar 2022, Grunnlagsfaktor(3), 0.beløp,7.5),
            Dag.Arbeidsdag(11 januar 2022, Grunnlagsfaktor(3), 0.beløp,7.5),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5)
        )

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(0.beløp, beløp)
    }

    @Test
    fun `Over 60 prosent arbeid gir 0 i utbetaling`() {
        val dager = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.6),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0),
            Dag.Helg(8 januar 2022, 0.0),
            Dag.Helg(9 januar 2022, 0.0),
            Dag.Arbeidsdag(10 januar 2022, Grunnlagsfaktor(3), 0.beløp,7.5),
            Dag.Arbeidsdag(11 januar 2022, Grunnlagsfaktor(3), 0.beløp,7.5),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0)
        )

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(0.beløp, beløp)
    }

    @Test
    fun `Akkurat 60 prosent arbeid gir 40 prosent utbetaling`() {
        val dager = listOf(
            Dag.Arbeidsdag(3 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(4 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(5 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(6 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0),
            Dag.Arbeidsdag(7 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0),
            Dag.Helg(8 januar 2022, 0.0),
            Dag.Helg(9 januar 2022, 0.0),
            Dag.Arbeidsdag(10 januar 2022, Grunnlagsfaktor(3), 0.beløp,7.5),
            Dag.Arbeidsdag(11 januar 2022, Grunnlagsfaktor(3), 0.beløp,7.5),
            Dag.Arbeidsdag(12 januar 2022, Grunnlagsfaktor(3), 0.beløp, 7.5),
            Dag.Arbeidsdag(13 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0),
            Dag.Arbeidsdag(14 januar 2022, Grunnlagsfaktor(3), 0.beløp, 0.0)
        )

        val meldeperiode = Meldeperiode()
        meldeperiode.leggTilDager(dager)

        val beløp = meldeperiode.sumForPeriode()
        assertEquals(3241.1.beløp, beløp)
    }
}
