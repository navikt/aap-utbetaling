package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjehistorikk

internal interface UtbetalingstidslinjeVisitor : UtbetalingsdagVisitor {
    fun preVisitUtbetalingstidslinjehistorikk(historikk: Utbetalingstidslinjehistorikk) {}
    fun preVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {}
    fun postVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {}
    fun postVisitUtbetalingstidslinjehistorikk(historikk: Utbetalingstidslinjehistorikk) {}
}