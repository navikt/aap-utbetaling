package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Vedtak.Companion.sortertPåDato
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Vedtakshistorikk {

    private val vedtakshistorikk = mutableListOf<Vedtak>()

    internal fun leggTilNyttVedtak(vedtakshendelse: Vedtakshendelse) {
        val vedtak = Vedtak.opprettFraVedtakshendelse(vedtakshendelse)
        vedtakshistorikk.add(vedtak)
    }

    internal fun oppdaterTidslinje(aktivitetstidslinje: Aktivitetstidslinje, melding: Meldepliktshendelse) {
        finnGjeldendeVedtak().oppdaterTidslinje(aktivitetstidslinje, melding)
    }

    private fun finnGjeldendeVedtak() = vedtakshistorikk.sortertPåDato().last()

    internal fun accept(visitor: SøkerVisitor) {
        visitor.visitVedtakshistorikk(vedtakshistorikk.toList())
        visitor.visitVedtakshistorikk(finnGjeldendeVedtak())
    }

}