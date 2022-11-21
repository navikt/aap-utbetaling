package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.Oppdragshistorikk
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjeModellApi
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

internal class Utbetalingstidslinjehistorikk(
    utbetalingstidslinjer: List<Utbetalingstidslinje> = emptyList()
) {

    private val utbetalingstidslinjer = utbetalingstidslinjer.toMutableList()
    private val sisteUtbetalingstidslinje get() = utbetalingstidslinjer.last()

    internal companion object {
        internal fun gjenopprett(utbetalingstidslinjeModellApi: List<UtbetalingstidslinjeModellApi>) =
            Utbetalingstidslinjehistorikk(utbetalingstidslinjeModellApi.map(Utbetalingstidslinje::gjenopprett))
    }

    internal fun add(utbetalingstidslinje: Utbetalingstidslinje) {
        utbetalingstidslinjer.add(utbetalingstidslinje)
    }

    internal fun barnetillegg(barnetillegg: Barnetillegg) {
        sisteUtbetalingstidslinje.barnetillegg(barnetillegg)
    }

    internal fun accept(visitor: MottakerVisitor) {
        visitor.preVisitUtbetalingstidslinjehistorikk(this)
        utbetalingstidslinjer.forEach { it.accept(visitor) }
        visitor.postVisitUtbetalingstidslinjehistorikk(this)
    }

    internal fun byggOppdrag(oppdragshistorikk: Oppdragshistorikk) {
        oppdragshistorikk.byggOppdrag(sisteUtbetalingstidslinje)
    }

    internal fun toDto() = utbetalingstidslinjer.map { it.toDto() }
}
