package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Vedtak.Companion.sortertPåDato
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.tidslinje.Tidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Vedtakshistorikk {

    private val vedtakshistorikk = mutableListOf<Vedtak>()

    internal fun leggTilNyttVedtak(vedtakshendelse: Vedtakshendelse) {
        val vedtak = Vedtak.opprettFraVedtakshendelse(vedtakshendelse)
        vedtakshistorikk.add(vedtak)
    }

    internal fun oppdaterTidslinje(tidslinje: Tidslinje, melding: Meldepliktshendelse) {
        finnGjeldendeVedtak().oppdaterTidslinje(tidslinje, melding)
    }

    private fun finnGjeldendeVedtak() = vedtakshistorikk.sortertPåDato().last()

    internal fun accept(visitor: SøkerVisitor) {
        visitor.visitVedtakshistorikk(vedtakshistorikk.toList())
        visitor.visitVedtakshistorikk(finnGjeldendeVedtak())
    }

}