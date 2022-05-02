package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.januar
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class AktivitetstidslinjeTest {

    @Test
    fun `Oppretter en meldeperiode når det kommmer inn en meldepliktshendelse`() {
        val aktivitetstidslinje = Aktivitetstidslinje()
        val hendelse = Meldepliktshendelse(listOf(BrukeraktivitetPerDag(3 januar 2022, 0.arbeidstimer, false)))

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse)

        val visitor = TidslinjeVisitor()
        aktivitetstidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
    }

    @Test
    fun `Oppretter en meldeperiode med 14 dager når det kommmer inn en meldepliktshendelse`() {
        val aktivitetstidslinje = Aktivitetstidslinje()
        val hendelse = Meldepliktshendelse((0 until 14L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, false)
        })

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse)

        val visitor = TidslinjeVisitor()
        aktivitetstidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(14, visitor.antallDager)
    }

    @Test
    fun `Oppretter en meldeperiode med 10 arbeidsdager og 4 helgedager når det kommmer inn en meldepliktshendelse`() {
        val aktivitetstidslinje = Aktivitetstidslinje()
        val hendelse = Meldepliktshendelse((0 until 14L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, false)
        })

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse)

        val visitor = TidslinjeVisitor()
        aktivitetstidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(4, visitor.antallHelgedager)
        assertEquals(10, visitor.antallArbeidsdager)
    }

    @Test
    fun `En meldepliktshendelse med 4 fraværsdager og 10 arbeidsdager gir 4 helgedager, 6 arbdager og 4 fraværsdager`() {
        val aktivitetstidslinje = Aktivitetstidslinje()
        val fraværsdager = (0 until 4L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, true)
        }
        val arbeidsdager = (0 until 10L).map {
            BrukeraktivitetPerDag((7 januar 2022).plusDays(it), 0.arbeidstimer, false)
        }
        val hendelse = Meldepliktshendelse(fraværsdager + arbeidsdager)

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse)

        val visitor = TidslinjeVisitor()
        aktivitetstidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(4, visitor.antallHelgedager)
        assertEquals(6, visitor.antallArbeidsdager)
        assertEquals(4, visitor.antallFraværsdager)
    }

    @Test
    fun `Første meldepliktshendelse oppretter en meldeperiode på 14 dager fra virkningsdato`() {
        val aktivitetstidslinje = Aktivitetstidslinje()
        val fraværsdager = (0 until 4L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, true)
        }
        val arbeidsdager = (0 until 10L).map {
            BrukeraktivitetPerDag((7 januar 2022).plusDays(it), 0.arbeidstimer, false)
        }
        val hendelse = Meldepliktshendelse(fraværsdager + arbeidsdager)

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse)

        val visitor = TidslinjeVisitor()
        aktivitetstidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(3 januar 2022, visitor.førsteDatoIMeldeperiode)
        assertEquals(16 januar 2022, visitor.sisteDatoIMeldeperiode)
    }

    @Test
    fun `Meldepliktshendelse som overlapper med en tidligere innsendt periode erstatter dagene`() {
        val aktivitetstidslinje = Aktivitetstidslinje()
        val hendelse1 = Meldepliktshendelse((0 until 14L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 0.arbeidstimer, false)
        })

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse1)

        val hendelse2 = Meldepliktshendelse((0 until 14L).map {
            BrukeraktivitetPerDag((3 januar 2022).plusDays(it), 4.arbeidstimer, false)
        })

        aktivitetstidslinje.håndterMeldepliktshendelse(hendelse2)

        val visitor = TidslinjeVisitor()
        aktivitetstidslinje.accept(visitor)

        assertEquals(1, visitor.antallMeldeperioder)
        assertEquals(4, visitor.antallHelgedager)
        assertEquals(10, visitor.antallArbeidsdager)
    }

    private class TidslinjeVisitor : SøkerVisitor {
        var antallMeldeperioder = 0
        var antallDager = 0
        var antallHelgedager = 0
        var antallArbeidsdager = 0
        var antallFraværsdager = 0
        var førsteDatoIMeldeperiode: LocalDate? = null
        var sisteDatoIMeldeperiode: LocalDate? = null

        override fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {
            antallMeldeperioder++
        }

        override fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {
            antallDager++
            antallHelgedager++
            if (førsteDatoIMeldeperiode == null) førsteDatoIMeldeperiode = dato
            sisteDatoIMeldeperiode = dato
        }

        override fun visitArbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) {
            antallDager++
            antallArbeidsdager++
            if (førsteDatoIMeldeperiode == null) førsteDatoIMeldeperiode = dato
            sisteDatoIMeldeperiode = dato
        }

        override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dato: LocalDate) {
            antallDager++
            antallFraværsdager++
            if (førsteDatoIMeldeperiode == null) førsteDatoIMeldeperiode = dato
            sisteDatoIMeldeperiode = dato
        }

        override fun visitVentedag(dato: LocalDate) {
            antallDager++
            if (førsteDatoIMeldeperiode == null) førsteDatoIMeldeperiode = dato
            sisteDatoIMeldeperiode = dato
        }
    }
}
