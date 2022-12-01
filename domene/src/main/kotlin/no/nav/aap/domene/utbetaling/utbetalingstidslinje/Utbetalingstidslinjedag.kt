package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Arbeidsprosent
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjedagModellApi
import no.nav.aap.domene.utbetaling.visitor.UtbetalingsdagVisitor
import java.time.LocalDate

internal sealed class Utbetalingstidslinjedag(protected val dato: LocalDate) {

    protected lateinit var arbeidsprosent: Arbeidsprosent

    internal abstract fun arbeidsprosent(arbeidsprosent: Arbeidsprosent)

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
        private val dagsats: Paragraf_11_20_2_ledd_2_punktum = årligYtelse.dagsats(),

        private val høyesteÅrligYtelseMedBarnetillegg: Paragraf_11_20_6_ledd = grunnlag.høyesteÅrligYtelseMedBarnetillegg(),
        //§11-20 2. ledd 2. punktum
        //TODO: Denne også heltall?
        private val høyesteBeløpMedBarnetillegg: Paragraf_11_20_2_ledd_2_punktum = høyesteÅrligYtelseMedBarnetillegg.høyesteBeløpMedBarnetillegg(),

        //§11-20 3.-5. ledd
        private val dagsatsMedBarnetillegg: Paragraf_11_20_3_5_ledd = dagsats.medBarnetillegg(barnetillegg),
        //§11-20 6. ledd
        private val beløpMedBarnetillegg: Beløp = dagsatsMedBarnetillegg.begrensTil(høyesteBeløpMedBarnetillegg),

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
            internal fun gjenopprett(modellApi: UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi): Utbetalingsdag {
                val utbetalingsdag = Utbetalingsdag(
                    dato = modellApi.dato,
                    fødselsdato = Fødselsdato(modellApi.fødselsdato),
                    grunnlagsfaktor = Grunnlagsfaktor(modellApi.grunnlagsfaktor),
                    grunnlagsfaktorJustertForAlder = Grunnlagsfaktor(modellApi.grunnlagsfaktorJustertForAlder),
                    barnetillegg = modellApi.barnetillegg.beløp,
                    grunnlag = Paragraf_11_19_3_ledd.gjenopprett(
                        dato = modellApi.grunnlag.dato,
                        grunnlagsfaktor = Grunnlagsfaktor(modellApi.grunnlag.grunnlagsfaktor),
                        grunnbeløp = modellApi.grunnlag.grunnbeløp.beløp,
                        grunnlag = modellApi.grunnlag.grunnlag.beløp,
                    ),
                    årligYtelse = Paragraf_11_20_1_ledd.gjenopprett(
                        faktorForReduksjonAvGrunnlag = modellApi.årligYtelse.faktorForReduksjonAvGrunnlag,
                        grunnlag = modellApi.årligYtelse.grunnlag.beløp,
                        årligYtelse = modellApi.årligYtelse.årligytelse.beløp,
                    ),
                    dagsats = Paragraf_11_20_2_ledd_2_punktum.gjenopprett(
                        antallDagerMedUtbetalingPerÅr = modellApi.dagsats.antallDagerMedUtbetalingPerÅr,
                        årligYtelse = modellApi.dagsats.årligYtelse.beløp,
                        dagsats = modellApi.dagsats.dagsats.beløp,
                    ),
                    høyesteÅrligYtelseMedBarnetillegg = Paragraf_11_20_6_ledd.gjenopprett(
                        maksFaktorAvGrunnlag = modellApi.høyesteÅrligYtelseMedBarnetillegg.maksFaktorAvGrunnlag,
                        grunnlag = modellApi.høyesteÅrligYtelseMedBarnetillegg.grunnlag.beløp,
                        høyesteÅrligYtelseMedBarnetillegg = modellApi.høyesteÅrligYtelseMedBarnetillegg.høyesteÅrligYtelseMedBarnetillegg.beløp,
                    ),
                    høyesteBeløpMedBarnetillegg = Paragraf_11_20_2_ledd_2_punktum.gjenopprett(
                        antallDagerMedUtbetalingPerÅr = modellApi.høyesteBeløpMedBarnetillegg.antallDagerMedUtbetalingPerÅr,
                        årligYtelse = modellApi.høyesteBeløpMedBarnetillegg.årligYtelse.beløp,
                        dagsats = modellApi.høyesteBeløpMedBarnetillegg.dagsats.beløp,
                    ),
                    dagsatsMedBarnetillegg = Paragraf_11_20_3_5_ledd.gjenopprett(
                        dagsats = modellApi.dagsatsMedBarnetillegg.dagsats.beløp,
                        barnetillegg = modellApi.dagsatsMedBarnetillegg.barnetillegg.beløp,
                        beløp = modellApi.dagsatsMedBarnetillegg.beløp.beløp,
                    ),
                    beløpMedBarnetillegg = modellApi.beløpMedBarnetillegg.beløp,
                )

                utbetalingsdag.beløp = modellApi.beløp.beløp
                utbetalingsdag.arbeidsprosent = Arbeidsprosent(modellApi.arbeidsprosent)

                return utbetalingsdag
            }
        }

        override fun arbeidsprosent(arbeidsprosent: Arbeidsprosent) {
            this.arbeidsprosent = arbeidsprosent
            beløp = beløpMedBarnetillegg.reduserMotArbeid(arbeidsprosent)
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
            arbeidsprosent = arbeidsprosent.toModellApi(),
        )
    }

    internal class IkkeUtbetalingsdag(dato: LocalDate) : Utbetalingstidslinjedag(dato) {

        internal companion object {
            internal fun gjenopprett(utbetalingstidslinjedagModellApi: UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi): IkkeUtbetalingsdag {
                val ikkeUtbetalingsdag = IkkeUtbetalingsdag(
                    dato = utbetalingstidslinjedagModellApi.dato
                )

                ikkeUtbetalingsdag.arbeidsprosent = Arbeidsprosent(utbetalingstidslinjedagModellApi.arbeidsprosent)

                return ikkeUtbetalingsdag
            }
        }

        override fun arbeidsprosent(arbeidsprosent: Arbeidsprosent) {
            this.arbeidsprosent = arbeidsprosent
        }

        override fun accept(visitor: UtbetalingsdagVisitor) {
            visitor.visitIkkeUtbetaling(this, dato)
        }

        override fun toModellApi() = UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi(
            dato = dato,
            arbeidsprosent = arbeidsprosent.toModellApi(),
        )
    }
}
