package no.nav.aap.domene.utbetaling.utbetalingstidslinje

internal class Utbetalingstidslinje(
    dager: List<Utbetalingsdag>
) {
    private val dager = dager.toMutableList()

    internal fun accept(visitor: UtbetalingstidslinjeVisitor){
        visitor.preVisitUtbetalingstidslinje(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitUtbetalingstidslinje(this)
    }
}

internal interface UtbetalingstidslinjeVisitor : UtbetalingsdagVisitor {
    fun preVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {}
    fun postVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {}
}
