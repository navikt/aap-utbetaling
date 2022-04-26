package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

internal class SøkerTest {

    @Test
    fun `Nytt vedtak oppdaterer vedtakshistorikk`() {
        val søker = Søker()
        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        )
        val visitor = TestVisitor()
        søker.accept(visitor)
        assertEquals(1, visitor.vedtakListeSize)
    }

    @Test
    fun `Nytt vedtak setter gjeldende vedtak i vedtakshistorikk`() {
        val søker = Søker()
        val vedtak1 = Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = LocalDate.now().minusDays(5),
            virkningsdato = LocalDate.now().minusDays(5)
        )
        søker.håndterVedtak(vedtak1)
        val visitor = TestVisitor()
        søker.accept(visitor)
        assertEquals(Vedtak.opprettFraVedtakshendelse(vedtak1), visitor.gjeldendeVedtak)

        val vedtak2 = Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = LocalDate.now().minusDays(2),
            virkningsdato = LocalDate.now().minusDays(2)
        )
        søker.håndterVedtak(vedtak2)
        søker.accept(visitor)
        assertEquals(Vedtak.opprettFraVedtakshendelse(vedtak2), visitor.gjeldendeVedtak)
    }

    @Test
    fun `Ny melding oppdaterer tidslinje`() {
        val søker = Søker()
        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(LocalDate.now(), 0.arbeidstimer, false)
                )
            )
        )
        val visitor = TestVisitor()
        søker.accept(visitor)

        assertEquals(1, visitor.antallDagerITidslinje)
    }

    @Test
    fun `uavhengige innmeldte brukeraktiviteter aggregeres`() {
        val visitor = TestVisitor()
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(LocalDate.now().minusDays(2), 0.arbeidstimer, false))
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(LocalDate.now().minusDays(1), 0.arbeidstimer, false))
            )
        )
        søker.accept(visitor)

        assertEquals(2, visitor.antallDagerITidslinje)
    }

    @Test
    fun `Flere uavhengige innmeldte brukeraktiviteter aggregeres`() {
        val visitor = TestVisitor()
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now()
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(LocalDate.now().minusDays(2), 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(LocalDate.now().minusDays(3), 0.arbeidstimer, false)
                )
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(LocalDate.now().minusDays(1), 0.arbeidstimer, false))
            )
        )
        søker.accept(visitor)

        assertEquals(3, visitor.antallDagerITidslinje)
    }

    private class TestVisitor : SøkerVisitor {
        var vedtakListeSize: Int = -1
        var antallDagerITidslinje: Int = 0
        lateinit var gjeldendeVedtak: Vedtak

        override fun visitVedtakshistorikk(vedtak: List<Vedtak>) {
            vedtakListeSize = vedtak.size
        }

        override fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {
            this.gjeldendeVedtak = gjeldendeVedtak
        }

        override fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {
            antallDagerITidslinje++
        }

        override fun visitArbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) {
            antallDagerITidslinje++
        }

        override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dato: LocalDate) {
            antallDagerITidslinje++
        }

        override fun visitVentedag(dato: LocalDate) {
            antallDagerITidslinje++
        }
    }
}