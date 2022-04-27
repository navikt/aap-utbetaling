package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor

internal class Utbetalingstidslinjehistorikk(
    utbetalingstidslinjer: List<Utbetalingstidslinje> = emptyList()
) {

    private val utbetalingstidslinjer = utbetalingstidslinjer.toMutableList()

    internal fun add(utbetalingstidslinje: Utbetalingstidslinje) {
        utbetalingstidslinjer.add(utbetalingstidslinje)
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.preVisitUtbetalingstidslinjehistorikk(this)
        utbetalingstidslinjer.forEach { it.accept(visitor) }
        visitor.postVisitUtbetalingstidslinjehistorikk(this)
    }
}