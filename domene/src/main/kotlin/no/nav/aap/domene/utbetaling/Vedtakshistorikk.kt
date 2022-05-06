package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Vedtak.Companion.sortertPåDato
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

internal class Vedtakshistorikk {

    private val vedtakshistorikk = mutableListOf<Vedtak>()

    internal fun leggTilNyttVedtak(vedtakshendelse: Vedtakshendelse) {
        val vedtak = Vedtak.opprettFraVedtakshendelse(vedtakshendelse)
        vedtakshistorikk.add(vedtak)
    }

    private fun finnGjeldendeVedtak() = vedtakshistorikk.sortertPåDato().last()

    internal fun utbetalingstidslinjeBuilder(barnetillegg: Barnetillegg) =
        finnGjeldendeVedtak().utbetalingstidslinjeBuilder(barnetillegg)

    internal fun accept(visitor: MottakerVisitor) {
        visitor.visitVedtakshistorikk(vedtakshistorikk.toList())
        visitor.visitVedtakshistorikk(finnGjeldendeVedtak())
    }

}