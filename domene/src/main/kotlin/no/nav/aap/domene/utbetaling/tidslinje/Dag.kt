package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.summerBeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.DayOfWeek
import java.time.LocalDate

internal sealed class Dag(
    protected val dato: LocalDate,
) {

    internal abstract fun arbeidstimer(): Double
    internal abstract fun beløp(arbeidsprosent: Double): Beløp

    internal abstract fun accept(visitor: DagVisitor)

    internal class Helg(
        dato: LocalDate,
        private val arbeidstimer: Double
    ) : Dag(dato) {

        override fun arbeidstimer() = arbeidstimer
        override fun beløp(arbeidsprosent: Double) = 0.beløp

        override fun accept(visitor: DagVisitor) {
            visitor.visitHelgedag()
        }
    }

    internal abstract class Beløpdag(
        dato: LocalDate,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val barnetillegg: Beløp
    ) : Dag(dato) {

        private companion object {
            private const val HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE = 0.6
        }

        private val grunnlag: Beløp = Grunnbeløp.årligYtelseINOK(dato, grunnlagsfaktor)
        private val dagsats = grunnlag * 0.66 / 260 //TODO: Heltall??
        private val høyestebeløpMedBarnetillegg = grunnlag * 0.9 / 260 //TODO: Denne også heltall??

        private val beløpMedBarnetillegg = minOf(høyestebeløpMedBarnetillegg, (dagsats + barnetillegg))

        internal open fun beløp() = beløpMedBarnetillegg
        override fun beløp(arbeidsprosent: Double) =
            if (arbeidsprosent > HØYESTE_ARBEIDSMENGDE_SOM_GIR_YTELSE) 0.beløp
            else beløpMedBarnetillegg * (1 - arbeidsprosent)
    }

    internal class Fraværsdag(
        dato: LocalDate,
        grunnlagsfaktor: Grunnlagsfaktor,
        barnetillegg: Beløp
    ) : Beløpdag(dato, grunnlagsfaktor, barnetillegg) {

        private var ignoreMe = false

        internal fun ignoreMe() {
            ignoreMe = true
        }

        override fun beløp(): Beløp = if(ignoreMe) {
            0.beløp
        } else {
            super.beløp()
        }

        override fun beløp(arbeidsprosent: Double): Beløp = if(ignoreMe) {
            0.beløp
        } else {
            super.beløp(arbeidsprosent)
        }

        override fun arbeidstimer() = 0.0
        override fun accept(visitor: DagVisitor) {
            visitor.visitFraværsdag(this, beløp())
        }
    }

    internal class Ventedag(
        dato: LocalDate,
        grunnlagsfaktor: Grunnlagsfaktor,
        barnetillegg: Beløp
    ) : Beløpdag(dato, grunnlagsfaktor, barnetillegg) {

        override fun arbeidstimer() = 0.0
        override fun accept(visitor: DagVisitor) {
            visitor.visitVentedag(beløp())
        }
    }

    internal class Arbeidsdag(
        dato: LocalDate,
        grunnlagsfaktor: Grunnlagsfaktor,
        barnetillegg: Beløp,
        private val arbeidstimer: Double
    ) : Beløpdag(
        dato,
        grunnlagsfaktor,
        barnetillegg
    ) {
        override fun arbeidstimer() = arbeidstimer

        override fun accept(visitor: DagVisitor) = visitor.visitArbeidsdag(beløp())
    }

    internal companion object {
        internal fun Iterable<Dag>.summerArbeidstimer() = sumOf { it.arbeidstimer() }
        internal fun Iterable<Dag>.beregnBeløp(arbeidsprosent: Double): Beløp =
            map { it.beløp(arbeidsprosent) }.summerBeløp()

        private fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }
}

internal interface DagVisitor {
    fun visitHelgedag() {}
    fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dagbeløp: Beløp) {}
    fun visitVentedag(dagbeløp: Beløp) {}
    fun visitArbeidsdag(dagbeløp: Beløp) {}
}

internal class FraværsdagVisitor : DagVisitor {
    private var tilstand: Tilstand = Tilstand.IngenFraværsdager()

    private lateinit var førsteFraværsdag: Dag.Fraværsdag

    override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dagbeløp: Beløp) {
        tilstand.visitFraværsdag(this, fraværsdag)
    }

    sealed class Tilstand {
        abstract fun visitFraværsdag(fraværsdagVisitor: FraværsdagVisitor, fraværsdag: Dag.Fraværsdag)

        class IngenFraværsdager : Tilstand() {
            override fun visitFraværsdag(fraværsdagVisitor: FraværsdagVisitor, fraværsdag: Dag.Fraværsdag) {
                fraværsdagVisitor.førsteFraværsdag = fraværsdag
                fraværsdagVisitor.tilstand = EnFraværsdag()
            }

        }

        class EnFraværsdag : Tilstand() {
            override fun visitFraværsdag(fraværsdagVisitor: FraværsdagVisitor, fraværsdag: Dag.Fraværsdag) {
                fraværsdagVisitor.førsteFraværsdag.ignoreMe()
                fraværsdag.ignoreMe()
            }
        }

        class FlereFraværsdager : Tilstand() {
            override fun visitFraværsdag(fraværsdagVisitor: FraværsdagVisitor, fraværsdag: Dag.Fraværsdag) {
                fraværsdag.ignoreMe()
            }
        }
    }

}
