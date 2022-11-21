package no.nav.aap.domene.utbetaling.modellapi

import java.time.LocalDate

data class BarnaModellApi (
    val barn: List<BarnModellApi>
)

data class BarnModellApi (
    //TODO: Trenger vi fødselsnummeret til barna?
    // val fødselsnummer: String,
    val fødselsdato: LocalDate
)
