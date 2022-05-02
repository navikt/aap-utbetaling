package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Barnetillegg.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import java.time.LocalDate

class Barnetillegg(barn: List<Barn>) {
    private val barn = mutableListOf(Barna(barn))
    private val nyesteInnslag get() = barn.last()

    private companion object {
        private val BARNETILLEGG = 27.beløp
    }

    internal fun barnetilleggForDag(dato: LocalDate) = BARNETILLEGG * nyesteInnslag.antallBarnUnder18År(dato)

    internal fun leggTilBarn(barna: List<Barn>) {
        barn.add(Barna(barna))
    }

    class Barna(
        private val barn: List<Barn>
    ) {
        internal fun antallBarnUnder18År(dato: LocalDate) = barn.antallBarnUnder18År(dato)
    }

    class Barn(
        private val fødselsdato: Fødselsdato
        // Legg til inntekt
    ) {
        internal companion object {
            internal fun Iterable<Barn>.antallBarnUnder18År(dato: LocalDate) =
                count { it.fødselsdato.erUnder18År(dato) }
        }
    }
}
