package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningInstitusjon
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjehistorikk
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

class Søker {
    private val aktivitetstidslinje = Aktivitetstidslinje()
    private val utbetalingstidslinjehistorikk = Utbetalingstidslinjehistorikk()
    private val vedtakshistorikk = Vedtakshistorikk()
    private val barnetillegg = Barnetillegg(emptyList())
    private val oppdragshistorikk = Oppdragshistorikk()

    internal fun håndterVedtak(vedtak: Vedtakshendelse) {
        vedtakshistorikk.leggTilNyttVedtak(vedtak)
    }

    internal fun håndterMeldeplikt(melding: Meldepliktshendelse) {
        aktivitetstidslinje.håndterMeldepliktshendelse(melding)

        val builder = vedtakshistorikk.utbetalingstidslinjeBuilder()
        val utbetalingstidslinje = builder.build(aktivitetstidslinje)
        utbetalingstidslinjehistorikk.add(utbetalingstidslinje)


        // behov -> slå opp barn og institusjon
    }

    internal fun håndterLøsning(løsning: LøsningBarn) {
        løsning.leggTilBarn(barnetillegg)

        utbetalingstidslinjehistorikk.barnetillegg(barnetillegg)

        utbetalingstidslinjehistorikk.byggOppdrag(oppdragshistorikk)
    }

    internal fun håndterLøsning(løsningInstitusjon: LøsningInstitusjon) {

    }

    internal fun accept(visitor: SøkerVisitor) {
        aktivitetstidslinje.accept(visitor)
        utbetalingstidslinjehistorikk.accept(visitor)
        vedtakshistorikk.accept(visitor)
        oppdragshistorikk.accept(visitor)
    }
}
