package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.dto.DtoUtbetalingstidslinjedag
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.visitor.UtbetalingsdagVisitor
import java.time.LocalDate

internal sealed class Utbetalingstidslinjedag(
    protected val dato: LocalDate
) {

    protected var arbeidsprosent: Double = Double.NaN

    internal abstract fun arbeidsprosent(arbeidsprosent: Double)
    internal open fun barnetillegg(barnetillegg: Barnetillegg) {}

    internal abstract fun accept(visitor: UtbetalingsdagVisitor)
    internal abstract fun toDto(): DtoUtbetalingstidslinjedag

    internal class Utbetalingsdag(
        dato: LocalDate,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val barnetillegg: Beløp
    ) : Utbetalingstidslinjedag(dato) {

        internal companion object {
            private const val FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG = 0.66
            private const val MAKS_FAKTOR_AV_GRUNNLAG = 0.9
            private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260
        }

        private val grunnlag: Beløp = Grunnbeløp.grunnlagINOK(dato, grunnlagsfaktor)
        private val årligYtelse = grunnlag * FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG

        //TODO: Heltall??
        private val dagsats = årligYtelse / ANTALL_DAGER_MED_UTBETALING_PER_ÅR
        private val høyesteÅrligYtelseMedBarnetillegg = grunnlag * MAKS_FAKTOR_AV_GRUNNLAG

        //TODO: Denne også heltall?
        private val høyestebeløpMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg / ANTALL_DAGER_MED_UTBETALING_PER_ÅR
        private val beløpMedBarnetillegg = minOf(høyestebeløpMedBarnetillegg, (dagsats + barnetillegg))
        private lateinit var beløp: Beløp

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
            beløp = beløpMedBarnetillegg * (1 - arbeidsprosent)
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitUtbetalingMedBeløp(this, dato, beløp)
        }

        override fun toDto() = DtoUtbetalingstidslinjedag(
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor.toDto(),
            barnetillegg = barnetillegg.toDto(),
            grunnlag = grunnlag.toDto(),
            dagsats = dagsats.toDto(),
            høyestebeløpMedBarnetillegg = høyestebeløpMedBarnetillegg.toDto(),
            beløpMedBarnetillegg = beløpMedBarnetillegg.toDto(),
            beløp = beløp.toDto(),
            arbeidsprosent = arbeidsprosent
        )
    }

    internal class IkkeUtbetalingsdag(dato: LocalDate) : Utbetalingstidslinjedag(dato) {

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitIkkeUtbetaling(this, dato)
        }

        override fun toDto() = DtoUtbetalingstidslinjedag(
            dato = dato,
            grunnlagsfaktor = null,
            barnetillegg = null,
            grunnlag = null,
            dagsats = null,
            høyestebeløpMedBarnetillegg = null,
            beløpMedBarnetillegg = null,
            beløp = null,
            arbeidsprosent = arbeidsprosent
        )
    }
}
