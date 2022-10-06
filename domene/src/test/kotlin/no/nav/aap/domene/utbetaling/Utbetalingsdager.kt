package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjedag
import java.time.LocalDate

internal object Utbetalingsdager {
    private var seed = 3 januar 2022
        get() {
            val f = field
            field = field.plusDays(1)
            return f
        }

    internal fun resetSeed(dato: LocalDate = 3 januar 2022) {
        seed = dato
    }

    internal val Int.U get() = U()
    internal fun Int.U(grunnlagsfaktor: Number = 3, arbeidsprosent: Number = 1, barn: List<Barnetillegg.Barn> = emptyList()) = (1..this)
        .map {
            val dato = seed
            Utbetalingstidslinjedag.Utbetalingsdag(
                dato,
                Grunnlagsfaktor(grunnlagsfaktor),
                Barnetillegg().apply { leggTilBarn(barn) }.barnetilleggForDag(dato)
            ).apply {
                arbeidsprosent(arbeidsprosent.toDouble())
            }
        }

    internal val Int.I
        get() = (1..this)
            .map { Utbetalingstidslinjedag.IkkeUtbetalingsdag(seed) }

    internal val Int.S: List<Utbetalingstidslinjedag>
        get() {
            resetSeed(seed.plusDays(this.toLong()))
            return emptyList()
        }
}
