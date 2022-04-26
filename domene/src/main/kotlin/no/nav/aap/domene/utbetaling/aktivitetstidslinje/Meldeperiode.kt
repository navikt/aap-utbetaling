package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Meldeperiode(dager: List<Dag> = emptyList()) {
    private val dager: MutableList<Dag> = dager.toMutableList()

    internal fun leggTilDag(dag: Dag) {
        dager.add(dag)
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.preVisitMeldeperiode(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitMeldeperiode(this)
    }
}
