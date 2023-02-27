package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.entitet.Arbeidsprosent
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.NORMAL_ARBEIDSTIMER
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjedag
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor
import java.time.LocalDate

internal class UtbetalingstidslinjeBuilder(
    private val grunnlagsfaktor: Grunnlagsfaktor,
    private val fødselsdato: Fødselsdato,
    private val barnetillegg: Barnetillegg
) : MottakerVisitor {

    private var utbetalingstidslinje = Utbetalingstidslinje(emptyList())


    /**
     * Builderen traverserer en aktivitetstidslinje, og for hver periode prøver den å svare på tre spørsmål
     * 1 Skal det utbetales for alle dager?
     * 2 Skal det utbetales for bare noen av dagene (mottaker har hatt to eller flere fraværsdager)?
     * 3 Skal det ikke utbetales for noen dager (mottaker har jobbet mer enn 60% innenfor en periode)?
     * */
    private lateinit var vanligUtbetalingstidslinje: Utbetalingstidslinje // Spørsmål 1
    private lateinit var avvisteFraværsdagerUtbetalingstidslinje: Utbetalingstidslinje // Spørsmål 2
    private lateinit var avvisteDagerUtbetalingstidslinje: Utbetalingstidslinje // Spørsmål 3

    private var arbeidstimerIPeriode: Arbeidstimer = 0.arbeidstimer

    // Skal kun summeres for hverdager som ikke er fraværsdager, bortsett fra hvis vi har kun en fraværsdag,
    // da skal den telles inn.
    private var normalarbeidstimerIPeriode: Arbeidstimer = 0.arbeidstimer

    private var tilstand: Tilstand = Tilstand.Start

    private companion object {
        private val HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE = Arbeidsprosent(0.6) // TODO Skal justeres ved vedtak
    }

    internal fun build(aktivitetstidslinje: Aktivitetstidslinje): Utbetalingstidslinje {
        aktivitetstidslinje.accept(this)
        return utbetalingstidslinje
    }

    override fun preVisitMeldeperiode(meldeperiode: Meldeperiode) {
        tilstand = Tilstand.Start
        vanligUtbetalingstidslinje = Utbetalingstidslinje(emptyList())
        avvisteDagerUtbetalingstidslinje = Utbetalingstidslinje(emptyList())
        avvisteFraværsdagerUtbetalingstidslinje = Utbetalingstidslinje(emptyList())
        arbeidstimerIPeriode = 0.arbeidstimer
        normalarbeidstimerIPeriode = 0.arbeidstimer
    }

    override fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {
        arbeidstimerIPeriode += arbeidstimer
    }

    override fun visitArbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) {
        arbeidstimerIPeriode += arbeidstimer
        normalarbeidstimerIPeriode += NORMAL_ARBEIDSTIMER

        tilstand.arbeidsdag(this, dato, arbeidstimer)
    }

    override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dato: LocalDate) {
        tilstand.fraværsdag(this, dato)
    }

    override fun postVisitMeldeperiode(meldeperiode: Meldeperiode) {
        tilstand.fullførMeldeperiode(this)
    }

    private fun fullførMeldeperiode(utbetalingstidslinjeUtenAvvisteArbeidsdager: Utbetalingstidslinje) {
        val arbeidsprosent = arbeidstimerIPeriode / normalarbeidstimerIPeriode
        val tidslinje = if (arbeidsprosent > HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE) avvisteDagerUtbetalingstidslinje
        else utbetalingstidslinjeUtenAvvisteArbeidsdager
        tidslinje.arbeidsprosent(arbeidsprosent)
        utbetalingstidslinje += tidslinje
    }

    private fun opprettUtbetaling(dato: LocalDate) = Utbetalingstidslinjedag.Utbetalingsdag(
        dato = dato,
        fødselsdato = fødselsdato,
        grunnlagsfaktorVedtak = grunnlagsfaktor,
        barnetillegg = barnetillegg.barnetilleggForDag(dato)
    )

    /**
     * Tilstanden sier noe om hvilken type aktivitetstidslinje vi har for denne perioden og dermed hvilken
     * utbetalingstidslinje vi ender opp med.
     */
    private sealed interface Tilstand {
        fun arbeidsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate, arbeidstimer: Arbeidstimer) {}
        fun fraværsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate) {}
        fun fullførMeldeperiode(builder: UtbetalingstidslinjeBuilder) {}

        /**
         * Dersom vi aldri forlater denne tilstanden, har vi en standard aktivitetstidslinje (ingen fraværsdager)
         */
        object Start : Tilstand {
            override fun arbeidsdag(
                builder: UtbetalingstidslinjeBuilder,
                dato: LocalDate,
                arbeidstimer: Arbeidstimer
            ) {
                val utbetalingsdag = builder.opprettUtbetaling(dato)
                builder.vanligUtbetalingstidslinje += utbetalingsdag
                builder.avvisteFraværsdagerUtbetalingstidslinje += utbetalingsdag

                val ikkeUtbetalingsdag = Utbetalingstidslinjedag.IkkeUtbetalingsdag(dato = dato)
                builder.avvisteDagerUtbetalingstidslinje += ikkeUtbetalingsdag
            }

            override fun fraværsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate) {
                val utbetalingsdag = builder.opprettUtbetaling(dato)
                builder.vanligUtbetalingstidslinje += utbetalingsdag

                val ikkeUtbetalingsdag = Utbetalingstidslinjedag.IkkeUtbetalingsdag(dato = dato)
                builder.avvisteFraværsdagerUtbetalingstidslinje += ikkeUtbetalingsdag
                builder.avvisteDagerUtbetalingstidslinje += ikkeUtbetalingsdag

                builder.tilstand = EnFraværsdag
            }

            override fun fullførMeldeperiode(builder: UtbetalingstidslinjeBuilder) {
                builder.fullførMeldeperiode(builder.vanligUtbetalingstidslinje)
            }
        }

        /** Dersom aktivitetstidslinjen kun har en fraværsdag, så ender vi i denne tilstanden
         * En fraværsdag i en periode er ok og skal ikke "telles" §11-8 3. ledd
         */
        object EnFraværsdag : Tilstand {
            override fun arbeidsdag(
                builder: UtbetalingstidslinjeBuilder,
                dato: LocalDate,
                arbeidstimer: Arbeidstimer
            ) {
                val utbetalingsdag = builder.opprettUtbetaling(dato)
                builder.vanligUtbetalingstidslinje += utbetalingsdag
                builder.avvisteFraværsdagerUtbetalingstidslinje += utbetalingsdag

                val ikkeUtbetalingsdag = Utbetalingstidslinjedag.IkkeUtbetalingsdag(dato = dato)
                builder.avvisteDagerUtbetalingstidslinje += ikkeUtbetalingsdag
            }

            override fun fraværsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate) {
                val ikkeUtbetalingsdag = Utbetalingstidslinjedag.IkkeUtbetalingsdag(dato = dato)
                builder.avvisteFraværsdagerUtbetalingstidslinje += ikkeUtbetalingsdag
                builder.avvisteDagerUtbetalingstidslinje += ikkeUtbetalingsdag

                builder.tilstand = MerEnnEnFraværsdag
            }

            override fun fullførMeldeperiode(builder: UtbetalingstidslinjeBuilder) {
                // Siden en fraværsdag ikke telles i visitoren, men skal telle i perioden, må vi legge til arbeidstid for denne
                builder.normalarbeidstimerIPeriode += NORMAL_ARBEIDSTIMER
                builder.fullførMeldeperiode(builder.vanligUtbetalingstidslinje)
            }
        }

        /**
         * Dersom aktivitetstidslinjen har mer enn en fraværsdag ender vi i denne tilstanden
         */
        object MerEnnEnFraværsdag : Tilstand {
            override fun arbeidsdag(
                builder: UtbetalingstidslinjeBuilder,
                dato: LocalDate,
                arbeidstimer: Arbeidstimer
            ) {
                val utbetalingsdag = builder.opprettUtbetaling(dato)
                builder.avvisteFraværsdagerUtbetalingstidslinje += utbetalingsdag

                val ikkeUtbetalingsdag = Utbetalingstidslinjedag.IkkeUtbetalingsdag(dato = dato)
                builder.avvisteDagerUtbetalingstidslinje += ikkeUtbetalingsdag
            }

            override fun fraværsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate) {
                val ikkeUtbetalingsdag = Utbetalingstidslinjedag.IkkeUtbetalingsdag(dato = dato)
                builder.avvisteFraværsdagerUtbetalingstidslinje += ikkeUtbetalingsdag
                builder.avvisteDagerUtbetalingstidslinje += ikkeUtbetalingsdag
            }

            override fun fullførMeldeperiode(builder: UtbetalingstidslinjeBuilder) {
                builder.fullførMeldeperiode(builder.avvisteFraværsdagerUtbetalingstidslinje)
            }
        }
    }
}
