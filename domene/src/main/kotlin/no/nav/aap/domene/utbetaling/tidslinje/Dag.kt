package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.DayOfWeek
import java.time.LocalDate

internal class Dag(
    private val dato: LocalDate,
    private val grunnlagsfaktor: Grunnlagsfaktor
) {

    companion object {
        fun opprettDag(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor) = Dag(
            dato = dato,
            grunnlagsfaktor = grunnlagsfaktor
        )
    }

    private fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
}
