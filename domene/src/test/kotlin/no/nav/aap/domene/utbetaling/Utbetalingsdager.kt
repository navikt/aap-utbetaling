package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingsdag
import java.time.LocalDate

object Utbetalingsdager {
    internal var seed = 3 januar 2022
        get() {
            val f = field
            field = field.plusDays(1)
            return f
        }

    internal fun resetSeed(dato: LocalDate = 3 januar 2022) {
        seed = dato
    }

    internal val Int.U get() = U()
    internal fun Int.U(grunnlagsfaktor: Number = 3, arbeidsprosent: Number = 1, barnetillegg: Number = 0) = (1..this)
        .map { Utbetalingsdag.Utbetaling(seed, Grunnlagsfaktor(grunnlagsfaktor), arbeidsprosent.toDouble(), barnetillegg.beløp) }

    internal val Int.I get() = (1..this)
        .map { Utbetalingsdag.IkkeUtbetaling(seed) }
}
