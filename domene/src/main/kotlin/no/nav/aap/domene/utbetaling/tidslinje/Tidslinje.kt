package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Tidslinje {
    private val meldeperioder = mutableListOf<Meldeperiode>()

    internal fun håndterMeldepliktshendelse(meldepliktshendelse: Meldepliktshendelse, grunnlagsfaktor: Grunnlagsfaktor) {
        val meldeperiode = Meldeperiode()
        meldepliktshendelse.populerMeldeperiode(meldeperiode, grunnlagsfaktor)
        meldeperioder.add(meldeperiode)
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.preVisitTidslinje(this)
        meldeperioder.forEach { it.accept(visitor) }
        visitor.postVisitTidslinje(this)
    }
}
