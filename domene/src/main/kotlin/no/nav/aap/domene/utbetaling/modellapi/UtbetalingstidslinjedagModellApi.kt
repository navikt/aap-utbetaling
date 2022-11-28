package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjedag
import java.time.LocalDate

data class UtbetalingstidslinjeModellApi(
    val dager: List<UtbetalingstidslinjedagModellApi>
)

interface UtbetalingstidslinjedagModellApiVisitor {
    fun visitUtbetalingsdag(utbetalingsdag: UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi)
    fun visitIkkeUtbetalingsdag(ikkeUtbetalingsdag: UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi)
}

sealed class UtbetalingstidslinjedagModellApi {
    abstract fun accept(visitor: UtbetalingstidslinjedagModellApiVisitor)
    internal abstract fun gjenopprett(): Utbetalingstidslinjedag

    data class UtbetalingsdagModellApi(
        val dato: LocalDate,
        val fødselsdato: LocalDate,
        val grunnlagsfaktor: Double,
        val grunnlagsfaktorJustertForAlder: Double,
        val barnetillegg: Double,
        val grunnlag: Paragraf_11_19_3_leddModellApi,
        val årligYtelse: Paragraf_11_20_1_ledd_ModellApi,
        val dagsats: Paragraf_11_20_2_ledd_2_punktum_ModellApi,
        val høyesteÅrligYtelseMedBarnetillegg: Paragraf_11_20_6_leddModellApi,
        val høyesteBeløpMedBarnetillegg: Double,
        val dagsatsMedBarnetillegg: Double,
        val beløpMedBarnetillegg: Double,
        val beløp: Double,
        val arbeidsprosent: Double
    ) : UtbetalingstidslinjedagModellApi() {
        override fun accept(visitor: UtbetalingstidslinjedagModellApiVisitor) {
            visitor.visitUtbetalingsdag(this)
        }

        override fun gjenopprett() =
            Utbetalingstidslinjedag.Utbetalingsdag.gjenopprett(this)
    }

    data class IkkeUtbetalingsdagModellApi(
        val dato: LocalDate,
        val arbeidsprosent: Double
    ) : UtbetalingstidslinjedagModellApi() {
        override fun accept(visitor: UtbetalingstidslinjedagModellApiVisitor) {
            visitor.visitIkkeUtbetalingsdag(this)
        }

        override fun gjenopprett() =
            Utbetalingstidslinjedag.IkkeUtbetalingsdag.gjenopprett(this)
    }
}
