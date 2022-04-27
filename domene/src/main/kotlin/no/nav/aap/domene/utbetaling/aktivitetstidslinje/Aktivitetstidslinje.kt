package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Aktivitetstidslinje(meldeperioder: List<Meldeperiode> = emptyList()) {

    private val meldeperioder: MutableList<Meldeperiode> = meldeperioder.toMutableList()

    internal fun håndterMeldepliktshendelse(
        meldepliktshendelse: Meldepliktshendelse
    ) {
        val meldeperiode = Meldeperiode()
        meldepliktshendelse.populerMeldeperiode(meldeperiode)
        meldeperioder.add(meldeperiode)
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.preVisitTidslinje(this)
        meldeperioder.forEach { it.accept(visitor) }
        visitor.postVisitTidslinje(this)
    }
}
