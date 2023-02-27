package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import java.time.LocalDate

data class LøsningModellApi(
    val barn: List<LøsningBarnModellApi>
) {
    fun håndter(mottakerModellApi: MottakerModellApi): MottakerModellApi {
        val mottaker = Mottaker.gjenopprett(mottakerModellApi)
        mottaker.håndterLøsning(LøsningBarn(barn.map { Barnetillegg.Barn(Fødselsdato(it.fødselsdato)) }))
        return mottaker.toModellApi()
    }
}

data class LøsningBarnModellApi(
    val fødselsdato: LocalDate
)
