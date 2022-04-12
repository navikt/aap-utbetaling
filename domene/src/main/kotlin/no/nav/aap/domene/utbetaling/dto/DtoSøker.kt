package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate

data class DtoSøker(
    val personident: String,
    val fødselsdato: LocalDate
)

