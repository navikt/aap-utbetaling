package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.tidslinje.Meldepliktsmelding
import no.nav.aap.domene.utbetaling.tidslinje.Tidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Søker {
    private val tidslinje = Tidslinje()
    private val vedtakshistorikk = Vedtakshistorikk()

    internal fun håndterVedtak(vedtak: Vedtakshendelse) {
        vedtakshistorikk.leggTilNyttVedtak(vedtak)
    }

    internal fun håndterMeldeplikt(melding: Meldepliktsmelding) {
        vedtakshistorikk.oppdaterTidslinje(tidslinje, melding)
        // behov -> slå opp barn og institusjon
    }

    internal fun accept(visitor: SøkerVisitor) {
        vedtakshistorikk.accept(visitor)
        tidslinje.accept(visitor)
    }
}

