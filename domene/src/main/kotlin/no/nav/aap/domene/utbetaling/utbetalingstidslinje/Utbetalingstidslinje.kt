package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjeModellApi
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjedagModellApi
import no.nav.aap.domene.utbetaling.visitor.UtbetalingstidslinjeVisitor

internal class Utbetalingstidslinje(
    dager: List<Utbetalingstidslinjedag>
) {
    private val dager = dager.toMutableList()

    internal companion object {
        internal fun gjenopprett(utbetalingstidslinjeModellApi: UtbetalingstidslinjeModellApi) =
            Utbetalingstidslinje(utbetalingstidslinjeModellApi.dager.map(UtbetalingstidslinjedagModellApi::gjenopprett))
    }

    internal fun arbeidsprosent(arbeidsprosent: Double) {
        dager.forEach { it.arbeidsprosent(arbeidsprosent) }
    }

    internal operator fun plus(dag: Utbetalingstidslinjedag) = Utbetalingstidslinje(this.dager + dag)
    internal operator fun plus(other: Utbetalingstidslinje) = Utbetalingstidslinje(this.dager + other.dager)

    internal fun accept(visitor: UtbetalingstidslinjeVisitor) {
        visitor.preVisitUtbetalingstidslinje(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitUtbetalingstidslinje(this)
    }

    internal fun toModellApi() = UtbetalingstidslinjeModellApi(dager.map { it.toModellApi() })
}
