package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.dto.DtoUtbetalingstidslinjedag
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.visitor.UtbetalingsdagVisitor
import java.time.LocalDate

internal sealed class Utbetalingstidslinjedag(
    protected val dato: LocalDate,
    protected val dagtype: Type,
) {

    protected enum class Type {
        UTBETALINGSDAG,
        IKKE_UTBETALINGSDAG,
    }

    protected var arbeidsprosent: Double = Double.NaN

    internal companion object {
        internal fun gjenopprett(dtoUtbetalingstidslinjedag: DtoUtbetalingstidslinjedag) =
            when (enumValueOf<Type>(dtoUtbetalingstidslinjedag.type)) {
                Type.UTBETALINGSDAG -> Utbetalingsdag.gjenopprett(dtoUtbetalingstidslinjedag)
                Type.IKKE_UTBETALINGSDAG -> IkkeUtbetalingsdag.gjenopprett(dtoUtbetalingstidslinjedag)
            }
    }

    internal abstract fun arbeidsprosent(arbeidsprosent: Double)
    internal open fun barnetillegg(barnetillegg: Barnetillegg) {}

    internal abstract fun accept(visitor: UtbetalingsdagVisitor)
    internal abstract fun toDto(): DtoUtbetalingstidslinjedag

    internal class Utbetalingsdag private constructor(
        dato: LocalDate,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val barnetillegg: Beløp,
        private val grunnlag: Beløp = Grunnbeløp.grunnlagINOK(dato, grunnlagsfaktor),
        private val årligYtelse: Beløp = grunnlag * FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG,

        //TODO: Heltall??
        private val dagsats: Beløp = årligYtelse / ANTALL_DAGER_MED_UTBETALING_PER_ÅR,
        private val høyesteÅrligYtelseMedBarnetillegg: Beløp = grunnlag * MAKS_FAKTOR_AV_GRUNNLAG,

        //TODO: Denne også heltall?
        private val høyestebeløpMedBarnetillegg: Beløp = høyesteÅrligYtelseMedBarnetillegg / ANTALL_DAGER_MED_UTBETALING_PER_ÅR,
        private val dagsatsMedBarnetillegg: Beløp = dagsats + barnetillegg,
        private val beløpMedBarnetillegg: Beløp = minOf(høyestebeløpMedBarnetillegg, dagsatsMedBarnetillegg),
    ) : Utbetalingstidslinjedag(dato, Type.UTBETALINGSDAG) {

        private lateinit var beløp: Beløp

        internal constructor(
            dato: LocalDate,
            grunnlagsfaktor: Grunnlagsfaktor,
            barnetillegg: Beløp
        ) : this(
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor,
            barnetillegg = barnetillegg,
            grunnlag = Grunnbeløp.grunnlagINOK(dato, grunnlagsfaktor),
        )

        internal companion object {
            private const val FAKTOR_FOR_REDUKSJON_AV_GRUNNLAG = 0.66
            private const val MAKS_FAKTOR_AV_GRUNNLAG = 0.9
            private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260

            internal fun gjenopprett(dtoUtbetalingstidslinjedag: DtoUtbetalingstidslinjedag): Utbetalingsdag {
                val utbetalingsdag = Utbetalingsdag(
                    dato = dtoUtbetalingstidslinjedag.dato,
                    grunnlagsfaktor = Grunnlagsfaktor(requireNotNull(dtoUtbetalingstidslinjedag.grunnlagsfaktor)),
                    barnetillegg = requireNotNull(dtoUtbetalingstidslinjedag.barnetillegg).beløp,
                    grunnlag = requireNotNull(dtoUtbetalingstidslinjedag.grunnlag).beløp,
                    årligYtelse = requireNotNull(dtoUtbetalingstidslinjedag.årligYtelse).beløp,
                    dagsats = requireNotNull(dtoUtbetalingstidslinjedag.dagsats).beløp,
                    høyesteÅrligYtelseMedBarnetillegg = requireNotNull(dtoUtbetalingstidslinjedag.høyesteÅrligYtelseMedBarnetillegg).beløp,
                    høyestebeløpMedBarnetillegg = requireNotNull(dtoUtbetalingstidslinjedag.høyestebeløpMedBarnetillegg).beløp,
                    dagsatsMedBarnetillegg = requireNotNull(dtoUtbetalingstidslinjedag.dagsatsMedBarnetillegg).beløp,
                    beløpMedBarnetillegg = requireNotNull(dtoUtbetalingstidslinjedag.beløpMedBarnetillegg).beløp,
                )

                utbetalingsdag.beløp = requireNotNull(dtoUtbetalingstidslinjedag.beløp).beløp
                utbetalingsdag.arbeidsprosent = dtoUtbetalingstidslinjedag.arbeidsprosent

                return utbetalingsdag
            }
        }

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
            beløp = beløpMedBarnetillegg * (1 - arbeidsprosent)
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitUtbetalingMedBeløp(this, dato, beløp)
        }

        override fun toDto() = DtoUtbetalingstidslinjedag(
            type = dagtype.name,
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor.toDto(),
            barnetillegg = barnetillegg.toDto(),
            grunnlag = grunnlag.toDto(),
            årligYtelse = årligYtelse.toDto(),
            dagsats = dagsats.toDto(),
            høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toDto(),
            høyestebeløpMedBarnetillegg = høyestebeløpMedBarnetillegg.toDto(),
            dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.toDto(),
            beløpMedBarnetillegg = beløpMedBarnetillegg.toDto(),
            beløp = beløp.toDto(),
            arbeidsprosent = arbeidsprosent
        )
    }

    internal class IkkeUtbetalingsdag(dato: LocalDate) : Utbetalingstidslinjedag(dato, Type.IKKE_UTBETALINGSDAG) {

        internal companion object {
            internal fun gjenopprett(dtoUtbetalingstidslinjedag: DtoUtbetalingstidslinjedag): IkkeUtbetalingsdag {
                val ikkeUtbetalingsdag = IkkeUtbetalingsdag(
                    dato = dtoUtbetalingstidslinjedag.dato
                )

                ikkeUtbetalingsdag.arbeidsprosent = dtoUtbetalingstidslinjedag.arbeidsprosent

                return ikkeUtbetalingsdag
            }
        }

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitIkkeUtbetaling(this, dato)
        }

        override fun toDto() = DtoUtbetalingstidslinjedag(
            type = dagtype.name,
            dato = dato,
            grunnlagsfaktor = null,
            barnetillegg = null,
            grunnlag = null,
            årligYtelse = null,
            dagsats = null,
            høyesteÅrligYtelseMedBarnetillegg = null,
            høyestebeløpMedBarnetillegg = null,
            dagsatsMedBarnetillegg = null,
            beløpMedBarnetillegg = null,
            beløp = null,
            arbeidsprosent = arbeidsprosent
        )
    }
}
