package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate

data class DtoBarna (
    val barn: List<DtoBarn>
)

data class DtoBarn (
    //TODO: Trenger vi fødselsnummeret til barna?
    // val fødselsnummer: String,
    val fødselsdato: LocalDate
)
