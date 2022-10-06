package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import java.time.LocalDate

data class DtoLøsning(
    val barn: List<DtoLøsningBarn>
) {
    fun håndter(dtoMottaker: DtoMottaker): DtoMottaker {
        val mottaker = Mottaker.gjenopprett(dtoMottaker)
        mottaker.håndterLøsning(LøsningBarn(barn.map { Barnetillegg.Barn(Fødselsdato(it.fødselsdato)) }))
        return mottaker.toDto()
    }
}

data class DtoLøsningBarn(
    val fødselsdato: LocalDate
)
