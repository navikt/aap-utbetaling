package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.LocalDate

internal class BrukeraktivitetPerDag(
    private val dato: LocalDate
) {
    fun oppdaterTidslinje(tidslinje: Tidslinje, grunnlagsfaktor: Grunnlagsfaktor) {
        tidslinje.leggTilDag(dato, grunnlagsfaktor)
    }
}