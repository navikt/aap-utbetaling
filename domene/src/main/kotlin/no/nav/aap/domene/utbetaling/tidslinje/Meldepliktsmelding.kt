package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Hendelse

internal class Meldepliktsmelding(
    private val brukersAktivitet: List<BrukeraktivitetPerDag>
) : Hendelse() {

    internal fun oppdaterTidlinje(tidslinje: Tidslinje, grunnlagsfaktor: Grunnlagsfaktor) {
        brukersAktivitet.forEach { it.oppdaterTidslinje(tidslinje, grunnlagsfaktor) }
    }

}