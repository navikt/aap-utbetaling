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

    internal abstract fun arbeidstimer():Double

    internal abstract fun accept(visitor: DagVisitor)

    internal class Helg(
        dato: LocalDate,
        private val arbeidstimer: Double
    ) : Dag(dato) {

        override fun arbeidstimer() = arbeidstimer

        override fun accept(visitor: DagVisitor) {
            visitor.visitHelgedag()
        }
    }

    internal class Fraværsdag(
        dato: LocalDate
    ) : Dag(dato) {

        override fun arbeidstimer() = 0.0

        override fun accept(visitor: DagVisitor) {
            visitor.visitFraværsdag()
        }
    }

    internal class Ventedag(
        dato: LocalDate
    ) : Dag(dato) {

        override fun arbeidstimer() = 0.0

        override fun accept(visitor: DagVisitor) {
            visitor.visitVentedag()
        }
    }

    internal class Arbeidsdag(
        dato: LocalDate,
        private val grunnlagsfaktor: Grunnlagsfaktor,
        private val barnetillegg: Beløp,
        private val arbeidstimer: Double
    ) : Dag(dato) {

        private val grunnlag: Beløp = if (dato.erHelg()) 0.beløp else Grunnbeløp.årligYtelseINOK(dato, grunnlagsfaktor)
        private val dagsats = grunnlag * 0.66 / 260 //TODO: Heltall??
        private val høyestebeløpMedBarnetillegg = grunnlag * 0.9 / 260 //TODO: Denne også heltall??

        private val beløpMedBarnetillegg = minOf(høyestebeløpMedBarnetillegg, (dagsats + barnetillegg)).avrundet()

        override fun arbeidstimer() = arbeidstimer

        override fun accept(visitor: DagVisitor) = visitor.visitArbeidsdag(beløpMedBarnetillegg)

        companion object {
            fun opprettDag(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor, barnetillegg: Beløp) = Arbeidsdag(
                dato = dato,
                grunnlagsfaktor = grunnlagsfaktor,
                barnetillegg = barnetillegg,
                0.0 //TODO
            )

            fun Iterable<Arbeidsdag>.summer(fom: LocalDate, tom: LocalDate) =
                filter { it.dato in fom..tom }.map { it.beløpMedBarnetillegg }.summerBeløp()
        }

        private fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }

    internal companion object{
        internal fun Iterable<Dag>.summerArbeidstimer() = sumOf { it.arbeidstimer() }
    }
}

internal interface DagVisitor {
    fun visitHelgedag() {}
    fun visitFraværsdag() {}
    fun visitVentedag() {}
    fun visitArbeidsdag(dagsats: Beløp) {}
}
