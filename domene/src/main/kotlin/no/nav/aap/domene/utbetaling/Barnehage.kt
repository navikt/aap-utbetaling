package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Barnehage.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import java.time.LocalDate

class Barnehage(
    private val barn: List<Barn>
) {

    private companion object {
        private val BARNETILLEGG = 27.beløp
    }

    internal fun barnetilleggForDag(dato: LocalDate) = BARNETILLEGG * barn.antallBarnUnder18År(dato)

    class Barn(
        private val fødselsdato: Fødselsdato
    ) {
        internal companion object {
            internal fun Iterable<Barn>.antallBarnUnder18År(dato: LocalDate) =
                count { it.fødselsdato.erUnder18År(dato) }
        }
    }
}
