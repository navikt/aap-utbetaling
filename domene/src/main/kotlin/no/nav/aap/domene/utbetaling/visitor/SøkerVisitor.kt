package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.Vedtak
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Meldeperiode

internal interface SøkerVisitor : DagVisitor, UtbetalingstidslinjeVisitor, OppdragVisitor {

    fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {}
    fun visitVedtakshistorikk(vedtak: List<Vedtak>) {}

    fun preVisitTidslinje(aktivitetstidslinje: Aktivitetstidslinje) {}
    fun postVisitTidslinje(aktivitetstidslinje: Aktivitetstidslinje) {}

    fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {}
    fun postVisitMeldeperiode(meldeperiode: Meldeperiode) {}
}
