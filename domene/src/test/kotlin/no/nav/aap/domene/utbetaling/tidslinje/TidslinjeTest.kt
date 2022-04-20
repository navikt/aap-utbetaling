package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.januar
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TidslinjeTest {

    @Test
    fun `Oppretter en meldeperiode når det kommmer inn en meldepliktshendelse`() {
        val tidslinje = Tidslinje()
        val hendelse = Meldepliktshendelse(listOf(BrukeraktivitetPerDag(3 januar 2022, 0.arbeidstimer, false)))

        tidslinje.håndterMeldepliktshendelse(hendelse, Grunnlagsfaktor(3))

        val visitor = TidslinjeVisitor()
        tidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
    }

    @Test
    fun `Oppretter en meldeperiode med 14 dager når det kommmer inn en meldepliktshendelse`() {
        val tidslinje = Tidslinje()
        val hendelse = Meldepliktshendelse((0 until 14L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, false)
        })

        tidslinje.håndterMeldepliktshendelse(hendelse, Grunnlagsfaktor(3))

        val visitor = TidslinjeVisitor()
        tidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(14, visitor.antallDager)
    }

    @Test
    fun `Oppretter en meldeperiode med 10 arbeidsdager og 4 helgedager når det kommmer inn en meldepliktshendelse`() {
        val tidslinje = Tidslinje()
        val hendelse = Meldepliktshendelse((0 until 14L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, false)
        })

        tidslinje.håndterMeldepliktshendelse(hendelse, Grunnlagsfaktor(3))

        val visitor = TidslinjeVisitor()
        tidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(4, visitor.antallHelgedager)
        assertEquals(10, visitor.antallArbeidsdager)
    }

    @Test
    fun `En meldepliktshendelse med 4 fraværsdager og 10 arbeidsdager gir 4 helgedager, 6 arbdager og 4 fraværsdager`() {
        val tidslinje = Tidslinje()
        val fraværsdager = (0 until 4L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, true)
        }
        val arbeidsdager = (0 until 10L).map {
            BrukeraktivitetPerDag((7 januar 2022).plusDays(it), 0.arbeidstimer, false)
        }
        val hendelse = Meldepliktshendelse(fraværsdager + arbeidsdager)

        tidslinje.håndterMeldepliktshendelse(hendelse, Grunnlagsfaktor(3))

        val visitor = TidslinjeVisitor()
        tidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(4, visitor.antallHelgedager)
        assertEquals(6, visitor.antallArbeidsdager)
        assertEquals(4, visitor.antallFraværsdager)
    }

    private class TidslinjeVisitor : SøkerVisitor {
        var antallMeldeperioder = 0
        var antallDager = 0
        var antallHelgedager = 0
        var antallArbeidsdager = 0
        var antallFraværsdager = 0

        override fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {
            antallMeldeperioder++
        }

        override fun visitHelgedag(helgedag: Dag.Helg) {
            antallDager++
            antallHelgedager++
        }

        override fun visitArbeidsdag(dagbeløp: Beløp) {
            antallDager++
            antallArbeidsdager++
        }

        override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dagbeløp: Beløp) {
            antallDager++
            antallFraværsdager++
        }

        override fun visitVentedag(dagbeløp: Beløp) {
            antallDager++
        }
    }
}
