package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjedagModellApi
import no.nav.aap.domene.utbetaling.visitor.UtbetalingsdagVisitor
import java.time.LocalDate

internal sealed class Utbetalingstidslinjedag(protected val dato: LocalDate) {

    protected var arbeidsprosent: Double = Double.NaN

    internal abstract fun arbeidsprosent(arbeidsprosent: Double)

    internal abstract fun accept(visitor: UtbetalingsdagVisitor)
    internal abstract fun toModellApi(): UtbetalingstidslinjedagModellApi

    //TODO: Gå gjennom hvilke beløp som skal rundes av
    internal class Utbetalingsdag private constructor(
        dato: LocalDate,
        private val barnetillegg: Beløp,
        private val fødselsdato: Fødselsdato,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val grunnlagsfaktorJustertForAlder: Grunnlagsfaktor =
            fødselsdato.justerGrunnlagsfaktorForAlder(dato, grunnlagsfaktor),
        //§11-19 3. ledd
        private val grunnlag: Paragraf_11_19_3_ledd = Paragraf_11_19_3_ledd(dato, grunnlagsfaktorJustertForAlder),
        private val årligYtelse: Paragraf_11_20_1_ledd = grunnlag.årligYtelse(),
        private val dagsats: Paragraf_11_20_2_ledd_2_punktum = Paragraf_11_20_2_ledd_2_punktum(årligYtelse),

        private val høyesteÅrligYtelseMedBarnetillegg: Paragraf_11_20_6_ledd = Paragraf_11_20_6_ledd(grunnlag),
        //§11-20 2. ledd 2. punktum
        //TODO: Denne også heltall?
        private val høyesteBeløpMedBarnetillegg: Beløp = høyesteÅrligYtelseMedBarnetillegg / ANTALL_DAGER_MED_UTBETALING_PER_ÅR,

        //§11-20 3.-5. ledd
        private val dagsatsMedBarnetillegg: Beløp = dagsats + barnetillegg,
        //§11-20 6. ledd
        private val beløpMedBarnetillegg: Beløp = minOf(høyesteBeløpMedBarnetillegg, dagsatsMedBarnetillegg),

        ) : Utbetalingstidslinjedag(dato) {

        private lateinit var beløp: Beløp

        internal constructor(
            dato: LocalDate,
            fødselsdato: Fødselsdato,
            grunnlagsfaktorVedtak: Grunnlagsfaktor,
            barnetillegg: Beløp
        ) : this(
            dato,
            barnetillegg,
            fødselsdato,
            grunnlagsfaktorVedtak,
        )

        internal companion object {
            private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260

            internal fun gjenopprett(utbetalingstidslinjedagModellApi: UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi): Utbetalingsdag {
                val grunnlag = Paragraf_11_19_3_ledd.gjenopprett(
                    dato = requireNotNull(utbetalingstidslinjedagModellApi.grunnlag).dato,
                    grunnlagsfaktor = Grunnlagsfaktor(requireNotNull(utbetalingstidslinjedagModellApi.grunnlag).grunnlagsfaktor),
                    grunnbeløp = requireNotNull(utbetalingstidslinjedagModellApi.grunnlag).grunnbeløp.beløp,
                    grunnlag = requireNotNull(utbetalingstidslinjedagModellApi.grunnlag).grunnlag.beløp,
                )
                val årligYtelse = Paragraf_11_20_1_ledd.gjenopprett(
                    faktorForReduksjonAvGrunnlag = requireNotNull(utbetalingstidslinjedagModellApi.årligYtelse).faktorForReduksjonAvGrunnlag,
                    grunnlag = requireNotNull(utbetalingstidslinjedagModellApi.årligYtelse).grunnlag.beløp,
                    årligYtelse = requireNotNull(utbetalingstidslinjedagModellApi.årligYtelse).årligytelse.beløp,
                )

                val dagsats = Paragraf_11_20_2_ledd_2_punktum.gjenopprett(
                    antallDagerMedUtbetalingPerÅr = requireNotNull(utbetalingstidslinjedagModellApi.dagsats).antallDagerMedUtbetalingPerÅr,
                    årligYtelse = årligYtelse,
                    dagsats = requireNotNull(utbetalingstidslinjedagModellApi.dagsats).dagsats.beløp,
                )
                val høyesteÅrligYtelseMedBarnetillegg = Paragraf_11_20_6_ledd.gjenopprett(
                    maksFaktorAvGrunnlag = requireNotNull(utbetalingstidslinjedagModellApi.høyesteÅrligYtelseMedBarnetillegg).maksFaktorAvGrunnlag,
                    grunnlag = grunnlag,
                    høyesteÅrligYtelseMedBarnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.høyesteÅrligYtelseMedBarnetillegg).høyesteÅrligYtelseMedBarnetillegg.beløp,
                )
                val utbetalingsdag = Utbetalingsdag(
                    dato = utbetalingstidslinjedagModellApi.dato,
                    fødselsdato = Fødselsdato(utbetalingstidslinjedagModellApi.fødselsdato),
                    grunnlagsfaktor = Grunnlagsfaktor(requireNotNull(utbetalingstidslinjedagModellApi.grunnlagsfaktor)),
                    grunnlagsfaktorJustertForAlder = Grunnlagsfaktor(requireNotNull(utbetalingstidslinjedagModellApi.grunnlagsfaktorJustertForAlder)),
                    barnetillegg = requireNotNull(utbetalingstidslinjedagModellApi.barnetillegg).beløp,
                    grunnlag = grunnlag,
                    årligYtelse = årligYtelse,
                    dagsats = dagsats,
                    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg,
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
            visitor.visitUtbetaling(this, dato, beløp)
        }

        override fun toModellApi() = UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi(
            dato = dato,
            fødselsdato = fødselsdato.toDto(),
            grunnlagsfaktor = grunnlagsfaktor.toModellApi(),
            grunnlagsfaktorJustertForAlder = grunnlagsfaktorJustertForAlder.toModellApi(),
            barnetillegg = barnetillegg.toModellApi(),
            grunnlag = grunnlag.toModellApi(),
            årligYtelse = årligYtelse.toModellApi(),
            dagsats = dagsats.toModellApi(),
            høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toModellApi(),
            høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg.toModellApi(),
            dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.toModellApi(),
            beløpMedBarnetillegg = beløpMedBarnetillegg.toModellApi(),
            beløp = beløp.toModellApi(),
            arbeidsprosent = arbeidsprosent
        )
    }

    internal class IkkeUtbetalingsdag(dato: LocalDate) : Utbetalingstidslinjedag(dato) {

        internal companion object {
            internal fun gjenopprett(utbetalingstidslinjedagModellApi: UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi): IkkeUtbetalingsdag {
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

        override fun toModellApi() = UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi(
            dato = dato,
            arbeidsprosent = arbeidsprosent
        )
    }
}
