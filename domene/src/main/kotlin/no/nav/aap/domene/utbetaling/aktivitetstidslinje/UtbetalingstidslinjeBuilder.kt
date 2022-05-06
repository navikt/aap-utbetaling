package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Barnetillegg
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

    private lateinit var vanligUtbetalingstidslinje: Utbetalingstidslinje
    private lateinit var avvisteFraværsdagerUtbetalingstidslinje: Utbetalingstidslinje
    private lateinit var avvisteDagerUtbetalingstidslinje: Utbetalingstidslinje

    private var arbeidstimerIPeriode: Arbeidstimer = 0.arbeidstimer
    private var normalarbeidstimerIPeriode: Arbeidstimer = 0.arbeidstimer

    private var tilstand: Tilstand = Tilstand.Start

    private companion object {
        private const val HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE = 0.6 // TODO Skal justeres ved vedtak
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

    override fun visitVentedag(dato: LocalDate) {

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
        grunnlagsfaktor = fødselsdato.justerGrunnlagsfaktorForAlder(dato, grunnlagsfaktor),
        barnetillegg = barnetillegg.barnetilleggForDag(dato)
    )

    private sealed interface Tilstand {
        fun arbeidsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate, arbeidstimer: Arbeidstimer) {}
        fun fraværsdag(builder: UtbetalingstidslinjeBuilder, dato: LocalDate) {}
        fun fullførMeldeperiode(builder: UtbetalingstidslinjeBuilder) {}

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
                builder.normalarbeidstimerIPeriode += NORMAL_ARBEIDSTIMER
                builder.fullførMeldeperiode(builder.vanligUtbetalingstidslinje)
            }
        }

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
