package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.Vedtak
import no.nav.aap.domene.utbetaling.tidslinje.DagVisitor
import no.nav.aap.domene.utbetaling.tidslinje.Meldeperiode
import no.nav.aap.domene.utbetaling.tidslinje.Tidslinje

internal interface SøkerVisitor : DagVisitor {


    fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {}
    fun visitVedtakshistorikk(vedtak: List<Vedtak>) {}

    fun preVisitTidslinje(tidslinje: Tidslinje) {}
    fun postVisitTidslinje(tidslinje: Tidslinje) {}

    fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {}
    fun postVisitMeldeperiode(meldeperiode: Meldeperiode) {}

}