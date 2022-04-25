package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.Vedtak
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.DagVisitor
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Meldeperiode
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje

internal interface SøkerVisitor : DagVisitor {

    fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {}
    fun visitVedtakshistorikk(vedtak: List<Vedtak>) {}

    fun preVisitTidslinje(aktivitetstidslinje: Aktivitetstidslinje) {}
    fun postVisitTidslinje(aktivitetstidslinje: Aktivitetstidslinje) {}

    fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {}
    fun postVisitMeldeperiode(meldeperiode: Meldeperiode) {}
}
