package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.Barnehage
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.tidslinje.Tidslinje
import java.time.LocalDate

internal class Meldepliktshendelse(
    private val brukersAktivitet: List<BrukeraktivitetPerDag>
) : Hendelse() {

    internal fun oppdaterTidlinje(tidslinje: Tidslinje, grunnlagsfaktor: Grunnlagsfaktor, barn: Barnehage) {
        brukersAktivitet.forEach { it.oppdaterTidslinje(tidslinje, grunnlagsfaktor, barn) }
    }

}

internal class BrukeraktivitetPerDag(
    private val dato: LocalDate
) {
    fun oppdaterTidslinje(tidslinje: Tidslinje, grunnlagsfaktor: Grunnlagsfaktor, barn: Barnehage) {
        tidslinje.leggTilDag(dato, grunnlagsfaktor, barn.barnetilleggForDag(dato))
    }
}