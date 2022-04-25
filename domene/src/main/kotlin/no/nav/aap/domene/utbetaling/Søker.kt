package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningInstitusjon
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

class Søker {
    private val aktivitetstidslinje = Aktivitetstidslinje()
    private val vedtakshistorikk = Vedtakshistorikk()
    private val barn = Barnetillegg()

    internal fun håndterVedtak(vedtak: Vedtakshendelse) {
        vedtakshistorikk.leggTilNyttVedtak(vedtak)
    }

    internal fun håndterMeldeplikt(melding: Meldepliktshendelse) {
        vedtakshistorikk.oppdaterTidslinje(aktivitetstidslinje, melding)
        // behov -> slå opp barn og institusjon
    }

    internal fun håndterLøsning(løsning: LøsningBarn) {
        barn.håndterLøsning(løsning)
    }

    internal fun håndterLøsning(løsningInstitusjon: LøsningInstitusjon) {

    }

    internal fun accept(visitor: SøkerVisitor) {
        vedtakshistorikk.accept(visitor)
        aktivitetstidslinje.accept(visitor)
    }
}
