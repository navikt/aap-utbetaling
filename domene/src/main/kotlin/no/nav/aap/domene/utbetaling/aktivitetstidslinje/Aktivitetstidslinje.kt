package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import java.time.LocalDate

internal class Aktivitetstidslinje(meldeperioder: List<Meldeperiode> = emptyList()) {

    private val meldeperioder: MutableList<Meldeperiode> = meldeperioder.toMutableList()

    internal fun håndterMeldepliktshendelse(
        meldepliktshendelse: Meldepliktshendelse,
        grunnlagsfaktor: Grunnlagsfaktor,
        virkningsdato: LocalDate
    ) {
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
