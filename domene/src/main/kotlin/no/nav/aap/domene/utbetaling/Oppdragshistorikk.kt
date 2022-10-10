package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.dto.DtoOppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.OppdragBuilder
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.visitor.OppdragVisitor

internal class Oppdragshistorikk private constructor(
    oppdragshistorikk: List<Oppdrag>
) {
    internal constructor() : this(emptyList())

    private val oppdragshistorikk = oppdragshistorikk.toMutableList()
    private val sisteOppdrag get() = oppdragshistorikk.lastOrNull()

    internal companion object {
        internal fun gjenopprett(dtoOppdrag: List<DtoOppdrag>) =
            Oppdragshistorikk(dtoOppdrag.map(Oppdrag::gjenopprett))
    }

    internal fun byggOppdrag(sisteUtbetalingstidslinje: Utbetalingstidslinje) {
        oppdragshistorikk.add(OppdragBuilder().build(sisteUtbetalingstidslinje))
    }

    internal fun accept(oppdragVisitor: OppdragVisitor) {
        oppdragVisitor.preVisitOppdragshistorikk()
        oppdragshistorikk.forEach { it.accept(oppdragVisitor) }
        oppdragVisitor.postVisitOppdragshistorikk()
    }

    internal fun toDto() = oppdragshistorikk.map { it.toDto() }
}
