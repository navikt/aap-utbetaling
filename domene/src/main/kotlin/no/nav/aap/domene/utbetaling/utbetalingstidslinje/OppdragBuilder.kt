package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Fagområde
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Utbetalingslinje
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.OppdragBuilder.Linje.Companion.toOppdragLinjer
import java.time.LocalDate
import kotlin.math.roundToInt

internal class OppdragBuilder : UtbetalingstidslinjeVisitor {

    private var tilstand: Tilstand = Tilstand.NyLinje

    internal fun build(utbetalingstidslinje: Utbetalingstidslinje): Oppdrag {
        utbetalingstidslinje.accept(this)
        return Oppdrag(
            mottaker = "mottaker", //FIXME
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = linjer.toOppdragLinjer(),
        )
    }

    private class Linje(
        private val fom: LocalDate,
        private var tom: LocalDate,
        private val beløp: Beløp
    ) {
        fun oppdaterTom(dato: LocalDate) {
            this.tom = dato
        }

        fun harSammeBeløp(beløp: Beløp) = this.beløp == beløp

        companion object {
            fun Iterable<Linje>.toOppdragLinjer() = map {
                Utbetalingslinje(
                    fom = it.fom,
                    tom = it.tom,
                    beløp = it.beløp.toDto().roundToInt(),
                    aktuellDagsinntekt = it.beløp.toDto().roundToInt(), //FIXME
                    grad = 100 //FIXME
                )
            }
        }
    }

    private lateinit var linje: Linje
    private val linjer = mutableListOf<Linje>()

    override fun visitUtbetalingMedBeløp(dag: Utbetalingstidslinjedag.Utbetalingsdag, dato: LocalDate, beløp: Beløp) {
        tilstand.arbeidsdag(this, beløp, dato)
    }

    override fun visitIkkeUtbetaling(dag: Utbetalingstidslinjedag.IkkeUtbetalingsdag, dato: LocalDate) {
        tilstand.fraværsdag(this, dato)
    }

    private sealed interface Tilstand {

        fun arbeidsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {}
        fun fraværsdag(builder: OppdragBuilder, dato: LocalDate) {}

        object NyLinje : Tilstand {
            override fun arbeidsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {
                builder.linje = Linje(dato, dato, dagbeløp)
                builder.linjer.add(builder.linje)
                builder.tilstand = SammenhengendeLinje
            }
        }

        object SammenhengendeLinje : Tilstand {
            override fun arbeidsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {
                if (builder.linje.harSammeBeløp(dagbeløp)) {
                    builder.linje.oppdaterTom(dato)
                } else {
                    builder.linje = Linje(dato, dato, dagbeløp)
                    builder.linjer.add(builder.linje)
                }
            }

            override fun fraværsdag(builder: OppdragBuilder, dato: LocalDate) {
                builder.tilstand = NyLinje
            }
        }
    }
}
