package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.Barnetillegg
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import java.time.LocalDate

data class DtoLøsning(
    val barn: List<DtoLøsningBarn>
) {
    fun opprettLøsning() = LøsningBarn(barn.map { Barnetillegg.Barn(Fødselsdato(it.fødselsdato)) })
}

data class DtoLøsningBarn(
    val fødselsdato: LocalDate
)
