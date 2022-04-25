package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.NORMAL_ARBEIDSTIMER
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingsdag
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingsdag.Utbetaling.Companion.konverterTilIkkeUtbetaling
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import java.time.LocalDate

internal class UtbetalingstidslinjeBuilder : SøkerVisitor {

    private val alleDager = mutableListOf<Utbetalingsdag>()
    private val alleDagerMedUtbetalingIPeriode = mutableListOf<Utbetalingsdag.Utbetaling>()
    private val alleDagerUtenUtbetalingIPeriode = mutableListOf<Utbetalingsdag.IkkeUtbetaling>()
    private var arbeidstimerIPeriode: Arbeidstimer = 0.arbeidstimer
    private var normalarbeidstimerIPeriode: Arbeidstimer = 0.arbeidstimer

    private companion object {
        private const val HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE = 0.6 // TODO Skal justeres ved vedtak
    }

    internal fun build(aktivitetstidslinje: Aktivitetstidslinje): Utbetalingstidslinje {
        aktivitetstidslinje.accept(this)
        return Utbetalingstidslinje(alleDager)
    }

    override fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {
        alleDagerMedUtbetalingIPeriode.clear()
        arbeidstimerIPeriode = 0.arbeidstimer
        normalarbeidstimerIPeriode = 0.arbeidstimer
    }

    override fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {
        arbeidstimerIPeriode += arbeidstimer
    }

    override fun visitArbeidsdag(dagbeløp: Beløp, dato: LocalDate, arbeidstimer: Arbeidstimer) {
        arbeidstimerIPeriode += arbeidstimer
        normalarbeidstimerIPeriode += NORMAL_ARBEIDSTIMER
        val utbetalingsdag = Utbetalingsdag.Utbetaling(
            dato = dato,
            grunnlagsfaktor = Grunnlagsfaktor(0),
            barnetillegg = 0.beløp
        )
        alleDagerMedUtbetalingIPeriode.add(utbetalingsdag)
    }

    override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dagbeløp: Beløp, dato: LocalDate, ignoreMe: Boolean) {

    }

    override fun visitVentedag(dagbeløp: Beløp, dato: LocalDate) {

    }

    override fun postVisitMeldeperiode(meldeperiode: Meldeperiode) {
        val arbeidsprosent = arbeidstimerIPeriode / normalarbeidstimerIPeriode
        if (arbeidsprosent > HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE) {
            alleDagerUtenUtbetalingIPeriode.addAll(alleDagerMedUtbetalingIPeriode.konverterTilIkkeUtbetaling())
        } else {
            alleDagerMedUtbetalingIPeriode.forEach { it.arbeidsprosent(arbeidsprosent) }
            alleDager.addAll(alleDagerMedUtbetalingIPeriode)
        }
        alleDagerUtenUtbetalingIPeriode.forEach { it.arbeidsprosent(arbeidsprosent) }
        alleDager.addAll(alleDagerUtenUtbetalingIPeriode)
    }
}
