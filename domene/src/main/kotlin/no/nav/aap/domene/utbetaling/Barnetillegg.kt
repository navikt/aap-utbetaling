package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Barnetillegg.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import java.time.LocalDate

class Barnetillegg(barn: List<Barn>) {
    private val barn = barn.toMutableList()

    private companion object {
        private val BARNETILLEGG = 27.beløp
    }

    internal fun barnetilleggForDag(dato: LocalDate) = BARNETILLEGG * barn.antallBarnUnder18År(dato)

    internal fun håndterLøsning(løsning: LøsningBarn) {
        // Oppdater barnehagen med evt nye opplysninger?
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
