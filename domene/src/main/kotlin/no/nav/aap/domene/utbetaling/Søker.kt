package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.tidslinje.Tidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

class Søker {
    private val tidslinje = Tidslinje()
    private val vedtakshistorikk = Vedtakshistorikk()

    internal fun håndterVedtak(vedtak: Vedtakshendelse) {
        vedtakshistorikk.leggTilNyttVedtak(vedtak)
    }

    internal fun håndterMeldeplikt(melding: Meldepliktshendelse) {
        vedtakshistorikk.oppdaterTidslinje(tidslinje, melding)
        // behov -> slå opp barn og institusjon
    }

    internal fun accept(visitor: SøkerVisitor) {
        vedtakshistorikk.accept(visitor)
        tidslinje.accept(visitor)
    }
}

