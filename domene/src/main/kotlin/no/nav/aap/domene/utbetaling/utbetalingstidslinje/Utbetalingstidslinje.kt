package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.dto.DtoUtbetalingstidslinje
import no.nav.aap.domene.utbetaling.visitor.UtbetalingstidslinjeVisitor

internal class Utbetalingstidslinje(
    dager: List<Utbetalingstidslinjedag>
) {
    private val dager = dager.toMutableList()

    internal companion object {
        internal fun gjenopprett(dtoUtbetalingstidslinje: DtoUtbetalingstidslinje) =
            Utbetalingstidslinje(dtoUtbetalingstidslinje.dager.map(Utbetalingstidslinjedag::gjenopprett))
    }

    internal fun arbeidsprosent(arbeidsprosent: Double) {
        dager.forEach { it.arbeidsprosent(arbeidsprosent) }
    }

    internal fun barnetillegg(barnetillegg: Barnetillegg) {
        dager.forEach { it.barnetillegg(barnetillegg) }
    }

    internal operator fun plus(dag: Utbetalingstidslinjedag) = Utbetalingstidslinje(this.dager + dag)
    internal operator fun plus(other: Utbetalingstidslinje) = Utbetalingstidslinje(this.dager + other.dager)

    internal fun accept(visitor: UtbetalingstidslinjeVisitor) {
        visitor.preVisitUtbetalingstidslinje(this)
        dager.forEach { it.accept(visitor) }
        visitor.postVisitUtbetalingstidslinje(this)
    }

    internal fun toDto() = DtoUtbetalingstidslinje(dager.map { it.toDto() })
}
