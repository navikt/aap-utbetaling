package no.nav.aap.app.simulering

import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApi
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjedagModellApi
import no.nav.aap.domene.utbetaling.modellapi.UtbetalingstidslinjedagModellApiVisitor
import java.time.LocalDate

data class SimuleringResponse(
    val aktivitetstidslinje: List<Aktivitetsdag>,
    val utbetalingstidslinje: List<Utbetalingstidslinjedag>,
    val kombinerteDager: List<KombinertDag>,
) {
    data class KombinertDag(
        val aktivitetsdag: Aktivitetsdag,
        val utbetalingstidslinjedag: Utbetalingstidslinjedag?,
    )

    data class Aktivitetsdag(
        val dato: LocalDate,
        val arbeidstimer: Double?,
        val type: String,
    )

    data class Utbetalingstidslinjedag(
        val type: String,
        val dato: LocalDate,
        val grunnlagsfaktor: Double?,
        val barnetillegg: Double?,
        val grunnlag: Double?,
        val årligYtelse: Double?,
        val dagsats: Double?,
        val høyesteÅrligYtelseMedBarnetillegg: Double?,
        val høyesteBeløpMedBarnetillegg: Double?,
        val dagsatsMedBarnetillegg: Double?,
        val beløpMedBarnetillegg: Double?,
        val beløp: Double?,
        val arbeidsprosent: Double,
    )

    companion object {
        fun lagNy(endretMottakerMedBarn: MottakerModellApi): SimuleringResponse {
            val aktivitetstidslinje = endretMottakerMedBarn.aktivitetstidslinje.single().dager.map {
                Aktivitetsdag(
                    dato = it.dato,
                    arbeidstimer = it.arbeidstimer,
                    type = it.type,
                )
            }
            val utbetalingstidslinje = endretMottakerMedBarn.utbetalingstidslinjehistorikk.single().dager.map {
                object : UtbetalingstidslinjedagModellApiVisitor {
                    lateinit var dag: Utbetalingstidslinjedag

                    init {
                        it.accept(this)
                    }

                    override fun visitUtbetalingsdag(utbetalingsdag: UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi) {
                        dag = Utbetalingstidslinjedag(
                            type = "UTBETALINGSDAG",
                            dato = utbetalingsdag.dato,
                            grunnlagsfaktor = utbetalingsdag.grunnlagsfaktor,
                            barnetillegg = utbetalingsdag.barnetillegg,
                            grunnlag = utbetalingsdag.grunnlag.grunnlag,
                            årligYtelse = utbetalingsdag.årligYtelse.årligytelse,
                            dagsats = utbetalingsdag.dagsats.dagsats,
                            høyesteÅrligYtelseMedBarnetillegg = utbetalingsdag.høyesteÅrligYtelseMedBarnetillegg.høyesteÅrligYtelseMedBarnetillegg,
                            høyesteBeløpMedBarnetillegg = utbetalingsdag.høyesteBeløpMedBarnetillegg.dagsats,
                            dagsatsMedBarnetillegg = utbetalingsdag.dagsatsMedBarnetillegg.beløp,
                            beløpMedBarnetillegg = utbetalingsdag.beløpMedBarnetillegg,
                            beløp = utbetalingsdag.beløp,
                            arbeidsprosent = utbetalingsdag.arbeidsprosent,
                        )
                    }

                    override fun visitIkkeUtbetalingsdag(ikkeUtbetalingsdag: UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi) {
                        dag = Utbetalingstidslinjedag(
                            type = "IKKE_UTBETALINGSDAG",
                            dato = ikkeUtbetalingsdag.dato,
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
                            arbeidsprosent = ikkeUtbetalingsdag.arbeidsprosent,
                        )
                    }
                }.dag
            }

            val kombinerteDager = aktivitetstidslinje
                .fold(emptyList<KombinertDag>()) { acc, aktivitetsdag ->
                    val tidslinjeDag = utbetalingstidslinje.singleOrNull { it.dato == aktivitetsdag.dato }
                    acc + KombinertDag(aktivitetsdag, tidslinjeDag)
                }
            return SimuleringResponse(
                aktivitetstidslinje = aktivitetstidslinje,
                utbetalingstidslinje = utbetalingstidslinje,
                kombinerteDager = kombinerteDager,
            )
        }
    }
}
