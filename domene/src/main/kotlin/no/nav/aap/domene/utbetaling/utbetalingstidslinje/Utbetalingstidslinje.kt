package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg

internal class Utbetalingstidslinje(
    dager: List<Utbetalingsdag>
) {
    private val dager = dager.toMutableList()

    internal fun arbeidsprosent(arbeidsprosent: Double) {
        dager.forEach { it.arbeidsprosent(arbeidsprosent) }
    }

    internal fun barnetillegg(barnetillegg: Barnetillegg) {
        dager.forEach { it.barnetillegg(barnetillegg) }
    }

    internal operator fun plus(dag: Utbetalingsdag) = Utbetalingstidslinje(this.dager + dag)
    internal operator fun plus(other: Utbetalingstidslinje) = Utbetalingstidslinje(this.dager + other.dager)

    internal fun accept(visitor: UtbetalingstidslinjeVisitor) {
        visitor.preVisitUtbetalingstidslinje(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitUtbetalingstidslinje(this)
    }
}

internal interface UtbetalingstidslinjeVisitor : UtbetalingsdagVisitor {
    fun preVisitUtbetalingstidslinjehistorikk(historikk: Utbetalingstidslinjehistorikk) {}
    fun preVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {}
    fun postVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {}
    fun postVisitUtbetalingstidslinjehistorikk(historikk: Utbetalingstidslinjehistorikk) {}
}
