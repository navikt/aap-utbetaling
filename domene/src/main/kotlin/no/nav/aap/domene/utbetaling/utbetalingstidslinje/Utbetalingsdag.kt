package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.LocalDate

internal interface UtbetalingsdagVisitor {
    fun visitUtbetaling(dag: Utbetalingsdag.Utbetaling, dato: LocalDate) {}
    fun visitUtbetalingMedBeløp(dag: Utbetalingsdag.Utbetaling, dato: LocalDate, beløp: Beløp) {}
    fun visitIkkeUtbetaling(dag: Utbetalingsdag.IkkeUtbetaling, dato: LocalDate) {}
}

internal sealed class Utbetalingsdag(
    protected val dato: LocalDate
) {

    protected var arbeidsprosent: Double = Double.NaN

    internal abstract fun arbeidsprosent(arbeidsprosent: Double)
    internal open fun barnetillegg(barnetillegg: Barnetillegg) {}

    internal abstract fun accept(visitor: UtbetalingsdagVisitor)

    internal class Utbetaling(
        dato: LocalDate,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val barnetillegg: Beløp
    ) : Utbetalingsdag(dato) {

        internal companion object {
            private const val FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG = 0.66
            private const val MAKS_FAKTOR_AV_GRUNNLAG = 0.9
            private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260
        }

        private val grunnlag: Beløp = Grunnbeløp.årligYtelseINOK(dato, grunnlagsfaktor)
        private val dagsats =
            grunnlag * FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG / ANTALL_DAGER_MED_UTBETALING_PER_ÅR //TODO: Heltall??
        private val høyestebeløpMedBarnetillegg =
            grunnlag * MAKS_FAKTOR_AV_GRUNNLAG / ANTALL_DAGER_MED_UTBETALING_PER_ÅR //TODO: Denne også heltal
        private val beløpMedBarnetillegg = minOf(høyestebeløpMedBarnetillegg, (dagsats + barnetillegg))
        private lateinit var beløp: Beløp

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
            beløp = beløpMedBarnetillegg * (1 - arbeidsprosent)
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitUtbetalingMedBeløp(this, dato, beløp)
        }
    }

    internal class IkkeUtbetaling(dato: LocalDate) : Utbetalingsdag(dato) {

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitIkkeUtbetaling(this, dato)
        }
    }
}
