package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.OppdragVisitor
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.OppdragBuilder
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje

internal class Oppdragshistorikk {
    private val oppdragshistorikk: MutableList<Oppdrag> = mutableListOf()
    private val sisteOppdrag get() = oppdragshistorikk.lastOrNull()

    internal fun byggOppdrag(sisteUtbetalingstidslinje: Utbetalingstidslinje) {
        oppdragshistorikk.add(OppdragBuilder().build(sisteUtbetalingstidslinje))
    }

    internal fun accept(oppdragVisitor: OppdragVisitor) {
        oppdragVisitor.preVisitOppdragshistorikk()
        oppdragshistorikk.forEach { it.accept(oppdragVisitor) }
        oppdragVisitor.postVisitOppdragshistorikk()
    }
}
