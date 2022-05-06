package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

internal class Meldeperiode(dager: List<Dag> = emptyList()) {
    private val dager: MutableList<Dag> = dager.toMutableList()

    internal fun leggTilDag(dag: Dag) {
        dager.add(dag)
    }

    internal fun accept(visitor: MottakerVisitor) {
        visitor.preVisitMeldeperiode(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitMeldeperiode(this)
    }

    private fun sammenfallerMed(other: Meldeperiode) =
        this.dager.first().sammenfallerMed(other.dager.first())

    internal companion object {
        internal fun Iterable<Meldeperiode>.merge(other: Meldeperiode): List<Meldeperiode> {
            val index = this.indexOfFirst { it.sammenfallerMed(other) }
            if (index == -1) return this + other

            return this.mapIndexed { i, meldeperiode -> if (i == index) other else meldeperiode }
        }
    }
}
