package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Meldeperiode.Companion.merge
import no.nav.aap.domene.utbetaling.modellapi.MeldeperiodeModellApi
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

internal class Aktivitetstidslinje(meldeperioder: List<Meldeperiode> = emptyList()) {

    private var meldeperioder: List<Meldeperiode> = meldeperioder.toMutableList()

    internal companion object{
        internal fun gjenopprett(meldeperioderModellApi: List<MeldeperiodeModellApi>)=Aktivitetstidslinje(
            meldeperioder = meldeperioderModellApi.map { Meldeperiode.gjenopprett(it) }
        )
    }

    internal fun merge(other: Meldeperiode) {
        meldeperioder = this.meldeperioder.merge(other)
    }

    internal fun håndterMeldepliktshendelse(
        meldepliktshendelse: Meldepliktshendelse
    ) {
        val meldeperiode = Meldeperiode()
        meldepliktshendelse.populerMeldeperiode(meldeperiode)
        merge(meldeperiode)
    }

    internal fun accept(visitor: MottakerVisitor) {
        visitor.preVisitTidslinje(this)
        meldeperioder.forEach { it.accept(visitor) }
        visitor.postVisitTidslinje(this)
    }

    internal fun toDto(): List<MeldeperiodeModellApi> = meldeperioder.map { it.toDto() }
}
