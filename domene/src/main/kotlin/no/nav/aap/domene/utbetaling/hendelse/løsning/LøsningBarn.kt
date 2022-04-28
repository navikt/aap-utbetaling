package no.nav.aap.domene.utbetaling.hendelse.løsning

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.hendelse.Hendelse

class LøsningBarn(
    private val barna: List<Barnetillegg.Barn>
) : Hendelse() {
    internal fun leggTilBarn(barnetillegg: Barnetillegg) {
        barnetillegg.leggTilBarn(barna)
    }
}
