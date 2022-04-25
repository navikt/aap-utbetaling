package no.nav.aap.domene.utbetaling.utbetalingstidslinje

internal class Utbetalingstidslinje(
    dager: List<Utbetalingsdag>
) {
    private val dager = dager.toMutableList()
}
