package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Barnetillegg.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import java.time.LocalDate

internal class Barnetillegg {
    private val historikk = mutableListOf<Barna>()
    private val nyesteInnslag get() = historikk.last()

    private companion object {
        private val BARNETILLEGG = 27.beløp
    }

    internal fun barnetilleggForDag(dato: LocalDate): Beløp {
        if (historikk.isEmpty()) throw RuntimeException("Har ingen informasjon om barn")
        return BARNETILLEGG * nyesteInnslag.antallBarnUnder18År(dato)
    }

    internal fun leggTilBarn(barna: List<Barn>) {
        historikk.add(Barna(barna))
    }

    internal class Barna(
        private val barn: List<Barn>
    ) {
        internal fun antallBarnUnder18År(dato: LocalDate) = barn.antallBarnUnder18År(dato)
    }

    internal class Barn(
        private val fødselsdato: Fødselsdato
        // Legg til inntekt
    ) {
        internal companion object {
            internal fun Iterable<Barn>.antallBarnUnder18År(dato: LocalDate) =
                count { it.fødselsdato.erUnder18År(dato) }
        }
    }
}
