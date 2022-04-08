package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.tidslinje.BrukeraktivitetPerDag
import no.nav.aap.domene.utbetaling.tidslinje.Dag
import no.nav.aap.domene.utbetaling.tidslinje.Meldepliktsmelding
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

internal class SøkerTest {

    @Test
    fun `Nytt vedtak oppdaterer vedtakshistorikk`() {
        val søker = Søker()
        søker.håndterVedtak(Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = LocalDate.now(),
            virkningsdato = LocalDate.now()
        ))
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
        søker.håndterVedtak(Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = LocalDate.now(),
            virkningsdato = LocalDate.now()
        ))
        søker.håndterMeldepliktsmelding(Meldepliktsmelding(
            brukersAktivitet = listOf(
                BrukeraktivitetPerDag(LocalDate.now())
            )
        ))
        val visitor = TestVisitor()
        søker.accept(visitor)

        assertEquals(1, visitor.dagerListeSize)
    }

    private class TestVisitor : SøkerVisitor {
        var vedtakListeSize: Int = -1
        var dagerListeSize: Int = -1
        lateinit var gjeldendeVedtak: Vedtak

        override fun visitVedtakshistorikk(vedtak: List<Vedtak>) {
            vedtakListeSize = vedtak.size
        }

        override fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {
            this.gjeldendeVedtak = gjeldendeVedtak
        }

        override fun visitTidslinje(dager: List<Dag>) {
            this.dagerListeSize = dager.size
        }
    }
}