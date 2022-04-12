package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.summerBeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnbeløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.DayOfWeek
import java.time.LocalDate

internal class Dag(
    private val dato: LocalDate,
    private val grunnlagsfaktor: Grunnlagsfaktor,
    private val barnetillegg: Beløp,
) {

    private val grunnlag: Beløp = if(dato.erHelg()) 0.beløp else Grunnbeløp.årligYtelseINOK(dato, grunnlagsfaktor)
    private val minsteDagsats = grunnlag * 0.66 / 260
    private val høyesteDagsats = grunnlag * 0.9 / 260

    private val dagsats = minOf(høyesteDagsats, (minsteDagsats + barnetillegg)).avrundet()

    internal fun accept(visitor: DagVisitor) = visitor.visitDag(dagsats)

    companion object {
        fun opprettDag(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor, barnetillegg: Beløp) = Dag(
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor,
            barnetillegg = barnetillegg
        )

        fun Iterable<Dag>.summer(fom: LocalDate, tom: LocalDate) = filter { it.dato in fom..tom }.map { it.dagsats }.summerBeløp()
    }

    private fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
}

internal interface DagVisitor {
    fun visitDag(dagsats: Beløp) {}
}
