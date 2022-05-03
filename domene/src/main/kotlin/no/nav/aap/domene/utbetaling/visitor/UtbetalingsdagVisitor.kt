package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjedag
import java.time.LocalDate

internal interface UtbetalingsdagVisitor {
    fun visitUtbetaling(dag: Utbetalingstidslinjedag.Utbetalingsdag, dato: LocalDate) {}
    fun visitUtbetalingMedBeløp(dag: Utbetalingstidslinjedag.Utbetalingsdag, dato: LocalDate, beløp: Beløp) {}
    fun visitIkkeUtbetaling(dag: Utbetalingstidslinjedag.IkkeUtbetalingsdag, dato: LocalDate) {}
}