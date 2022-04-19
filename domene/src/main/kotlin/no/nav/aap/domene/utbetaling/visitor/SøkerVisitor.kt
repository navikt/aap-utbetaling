package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.Vedtak
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Arbeidsdag

internal interface SøkerVisitor {


    fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {}
    fun visitVedtakshistorikk(vedtak: List<Vedtak>) {}

    fun visitTidslinje(dager: List<Arbeidsdag>) {}

}