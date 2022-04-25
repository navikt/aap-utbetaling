package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.beregnBeløp
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.summerArbeidstimer
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.summerNormalArbeidstimer
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Meldeperiode(dager: List<Dag> = emptyList()) {
    private val dager: MutableList<Dag> = dager.toMutableList()

    internal fun leggTilDag(dag: Dag) {
        dager.add(dag)
    }

    internal fun leggTilDager(dag: Iterable<Dag>) {
        dager.addAll(dag)
    }

    internal fun beregnArbeidsprosent(): Double {
        accept(FraværsdagVisitor())
        return dager.summerArbeidstimer() / dager.summerNormalArbeidstimer()
    }

    internal fun sumForPeriode(): Beløp {
        val arbeidsprosent = beregnArbeidsprosent()
        return dager.beregnBeløp(arbeidsprosent)
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.preVisitMeldeperiode(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitMeldeperiode(this)
    }
}
