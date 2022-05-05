package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate

data class DtoMottaker(
    val personident: String,
    val fødselsdato: LocalDate
)

