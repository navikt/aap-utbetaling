package no.nav.aap.domene.utbetaling.utbetalingslinjer

import no.nav.aap.domene.utbetaling.entitet.Beløp
import java.time.LocalDate

internal enum class Fagområde(
    internal val verdi: String,
    private val beløpStrategy: (Beløp) -> Int,
    private val klassekode: Klassekode
) {
    Arbeidsavklaringspenger("AAP", { 0 }, Klassekode.RefusjonIkkeOpplysningspliktig);

    override fun toString() = verdi

    internal fun linje(fagsystemId: String, økonomi: Beløp, dato: LocalDate, grad: Int, beløp: Int) =
        Utbetalingslinje(dato, dato, Satstype.Daglig, beløpStrategy(økonomi), beløp, grad, fagsystemId, klassekode = klassekode)

    internal fun linje(fagsystemId: String, dato: LocalDate, grad: Int) =
        Utbetalingslinje(dato, dato, Satstype.Daglig, null, 0, grad, fagsystemId, klassekode = klassekode)

    internal fun oppdaterLinje(linje: Utbetalingslinje, dato: LocalDate, økonomi: Beløp, beløp: Int) {
        linje.beløp = beløpStrategy(økonomi)
        linje.aktuellDagsinntekt = beløp
        linje.fom = dato
    }

    internal fun kanLinjeUtvides(linje: Utbetalingslinje, økonomi: Beløp, grad: Int) =
        grad == linje.grad && (linje.beløp == null || linje.beløp == beløpStrategy(økonomi))

    internal companion object {
        private val map = values().associateBy(Fagområde::verdi)
        fun from(verdi: String) = requireNotNull(map[verdi]) { "Støtter ikke klassekode: $verdi" }
    }
}