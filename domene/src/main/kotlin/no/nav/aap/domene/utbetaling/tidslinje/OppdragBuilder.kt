package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.tidslinje.OppdragBuilder.Linje.Companion.toOppdragLinjer
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Fagområde
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Utbetalingslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import java.time.LocalDate
import kotlin.math.roundToInt

internal class OppdragBuilder : SøkerVisitor {

    private lateinit var oppdrag: Oppdrag
    private var tilstand: Tilstand = Tilstand.NyLinje

    internal fun build(tidslinje: Tidslinje): Oppdrag {
        tidslinje.accept(this)
        oppdrag = Oppdrag(
            mottaker = "mottaker", //FIXME
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = linjer.toOppdragLinjer(),
        )
        return oppdrag
    }

    private class Linje(
        private val fom: LocalDate,
        private var tom: LocalDate,
        private val beløp: Beløp
    ) {
        fun oppdaterTom(dato: LocalDate) {
            this.tom = dato
        }

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

    override fun visitArbeidsdag(dagbeløp: Beløp, dato: LocalDate) {
        tilstand.arbeidsdag(this, dagbeløp, dato)
    }

    override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dagbeløp: Beløp, dato: LocalDate, ignoreMe: Boolean) {
        if (ignoreMe) {
            tilstand.fraværsdag(this, dagbeløp, dato)
        } else {
            tilstand.arbeidsdag(this, dagbeløp, dato)
        }
    }

    private sealed interface Tilstand {

        fun arbeidsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {}
        fun fraværsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {}

        object NyLinje : Tilstand {
            override fun arbeidsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {
                builder.linje = Linje(dato, dato, dagbeløp)
                builder.linjer.add(builder.linje)
                builder.tilstand = SammenhengendeLinje
            }
        }

        object SammenhengendeLinje : Tilstand {
            override fun arbeidsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {
                builder.linje.oppdaterTom(dato)
            }

            override fun fraværsdag(builder: OppdragBuilder, dagbeløp: Beløp, dato: LocalDate) {
                builder.tilstand = NyLinje
            }
        }
    }
}
