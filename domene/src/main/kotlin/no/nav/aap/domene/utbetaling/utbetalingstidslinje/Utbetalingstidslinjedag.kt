package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjedagModellApi
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
        internal fun gjenopprett(utbetalingstidslinjedagModellApi: UtbetalingstidslinjedagModellApi) =
            when (enumValueOf<Type>(utbetalingstidslinjedagModellApi.type)) {
                Type.UTBETALINGSDAG -> Utbetalingsdag.gjenopprett(utbetalingstidslinjedagModellApi)
                Type.IKKE_UTBETALINGSDAG -> IkkeUtbetalingsdag.gjenopprett(utbetalingstidslinjedagModellApi)
            }
    }

    internal abstract fun arbeidsprosent(arbeidsprosent: Double)
    internal open fun barnetillegg(barnetillegg: Barnetillegg) {}

    internal abstract fun accept(visitor: UtbetalingsdagVisitor)
    internal abstract fun toDto(): UtbetalingstidslinjedagModellApi

    //TODO: Gå gjennom hvilke beløp som skal rundes av
    //TODO: Flytt kode for fastsetting av minstegrunnlag (2G) inn hit
    internal class Utbetalingsdag private constructor(
        dato: LocalDate,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val barnetillegg: Beløp,
        //§11-19 3. ledd
        private val grunnlag: Beløp = Grunnbeløp.grunnlagINOK(dato, grunnlagsfaktor),
        private val årligYtelse: Paragraf_11_20_1_ledd = Paragraf_11_20_1_ledd(grunnlag),
        private val dagsats: Paragraf_11_20_2_ledd_2_punktum = Paragraf_11_20_2_ledd_2_punktum(årligYtelse),

        //§11-20 6. ledd
        private val høyesteÅrligYtelseMedBarnetillegg: Beløp = grunnlag * MAKS_FAKTOR_AV_GRUNNLAG,
        //§11-20 2. ledd 2. punktum
        //TODO: Denne også heltall?
        private val høyesteBeløpMedBarnetillegg: Beløp = høyesteÅrligYtelseMedBarnetillegg / ANTALL_DAGER_MED_UTBETALING_PER_ÅR,

        //§11-20 3.-5. ledd
        private val dagsatsMedBarnetillegg: Beløp = dagsats + barnetillegg,
        //§11-20 6. ledd
        private val beløpMedBarnetillegg: Beløp = minOf(høyesteBeløpMedBarnetillegg, dagsatsMedBarnetillegg),

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
            private const val MAKS_FAKTOR_AV_GRUNNLAG = 0.9
            private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260

            internal fun gjenopprett(utbetalingstidslinjedagModellApi: UtbetalingstidslinjedagModellApi): Utbetalingsdag {
                val årligYtelse = Paragraf_11_20_1_ledd.gjenopprett(
                    faktorForReduksjonAvGrunnlag = requireNotNull(utbetalingstidslinjedagModellApi.årligYtelse).faktorForReduksjonAvGrunnlag,
                    inntektsgrunnlag = requireNotNull(utbetalingstidslinjedagModellApi.årligYtelse).inntektsgrunnlag.beløp,
                    årligYtelse = requireNotNull(utbetalingstidslinjedagModellApi.årligYtelse).årligytelse.beløp
                )

                val dagsats = Paragraf_11_20_2_ledd_2_punktum.gjenopprett(
                    antallDagerMedUtbetalingPerÅr = requireNotNull(utbetalingstidslinjedagModellApi.dagsats).antallDagerMedUtbetalingPerÅr,
                    årligYtelse = årligYtelse,
                    dagsats = requireNotNull(utbetalingstidslinjedagModellApi.dagsats).dagsats.beløp
                )
                val utbetalingsdag = Utbetalingsdag(
                    dato = utbetalingstidslinjedagModellApi.dato,
                    grunnlagsfaktor = Grunnlagsfaktor(requireNotNull(utbetalingstidslinjedagModellApi.grunnlagsfaktor)),
                    barnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.barnetillegg).beløp,
                    grunnlag = requireNotNull(utbetalingstidslinjedagModellApi.grunnlag).beløp,
                    årligYtelse = årligYtelse,
                    dagsats = dagsats,
                    høyesteÅrligYtelseMedBarnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.høyesteÅrligYtelseMedBarnetillegg).beløp,
                    høyesteBeløpMedBarnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.høyesteBeløpMedBarnetillegg).beløp,
                    dagsatsMedBarnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.dagsatsMedBarnetillegg).beløp,
                    beløpMedBarnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.beløpMedBarnetillegg).beløp,
                )

                utbetalingsdag.beløp = requireNotNull(utbetalingstidslinjedagModellApi.beløp).beløp
                utbetalingsdag.arbeidsprosent = utbetalingstidslinjedagModellApi.arbeidsprosent

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

        override fun toDto() = UtbetalingstidslinjedagModellApi(
            type = dagtype.name,
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor.toDto(),
            barnetillegg = barnetillegg.toDto(),
            grunnlag = grunnlag.toDto(),
            årligYtelse = årligYtelse.toDto(),
            dagsats = dagsats.toDto(),
            høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toDto(),
            høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg.toDto(),
            dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.toDto(),
            beløpMedBarnetillegg = beløpMedBarnetillegg.toDto(),
            beløp = beløp.toDto(),
            arbeidsprosent = arbeidsprosent
        )
    }

    internal class IkkeUtbetalingsdag(dato: LocalDate) : Utbetalingstidslinjedag(dato, Type.IKKE_UTBETALINGSDAG) {

        internal companion object {
            internal fun gjenopprett(utbetalingstidslinjedagModellApi: UtbetalingstidslinjedagModellApi): IkkeUtbetalingsdag {
                val ikkeUtbetalingsdag = IkkeUtbetalingsdag(
                    dato = utbetalingstidslinjedagModellApi.dato
                )

                ikkeUtbetalingsdag.arbeidsprosent = utbetalingstidslinjedagModellApi.arbeidsprosent

                return ikkeUtbetalingsdag
            }
        }

        override fun arbeidsprosent(arbeidsprosent: Double) {
            this.arbeidsprosent = arbeidsprosent
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitIkkeUtbetaling(this, dato)
        }

        override fun toDto() = UtbetalingstidslinjedagModellApi(
            type = dagtype.name,
            dato = dato,
            grunnlagsfaktor = null,
            barnetillegg = null,
            grunnlag = null,
            årligYtelse = null,
            dagsats = null,
            høyesteÅrligYtelseMedBarnetillegg = null,
            høyesteBeløpMedBarnetillegg = null,
            dagsatsMedBarnetillegg = null,
            beløpMedBarnetillegg = null,
            beløp = null,
            arbeidsprosent = arbeidsprosent
        )
    }
}
