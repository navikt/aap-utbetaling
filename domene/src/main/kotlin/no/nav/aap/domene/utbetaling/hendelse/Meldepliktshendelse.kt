package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.tidslinje.Tidslinje
import java.time.LocalDate

internal class Meldepliktshendelse(
    private val brukersAktivitet: List<BrukeraktivitetPerDag>
) : Hendelse() {

    internal fun oppdaterTidlinje(tidslinje: Tidslinje, grunnlagsfaktor: Grunnlagsfaktor, barnetillegg: Beløp) {
        brukersAktivitet.forEach { it.oppdaterTidslinje(tidslinje, grunnlagsfaktor, barnetillegg) }
    }

}

internal class BrukeraktivitetPerDag(
    private val dato: LocalDate
) {
    fun oppdaterTidslinje(tidslinje: Tidslinje, grunnlagsfaktor: Grunnlagsfaktor, barnetillegg: Beløp) {
        tidslinje.leggTilDag(dato, grunnlagsfaktor, barnetillegg)
    }
}