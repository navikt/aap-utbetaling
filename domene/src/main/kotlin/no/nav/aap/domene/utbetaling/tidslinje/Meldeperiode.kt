package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Companion.beregnBeløp
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Companion.summerArbeidstimer
import no.nav.aap.domene.utbetaling.tidslinje.Dag.Companion.summerNormalArbeidstimer
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Meldeperiode {
    private val dager = mutableListOf<Dag>()

    private companion object {
        private const val ARBEIDSTIMER_PER_UKE = 37.5
        private const val ARBEIDSTIMER_PER_PERIODE = ARBEIDSTIMER_PER_UKE * 2
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
