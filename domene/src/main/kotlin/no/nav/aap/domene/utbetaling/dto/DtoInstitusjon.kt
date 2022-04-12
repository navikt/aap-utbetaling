package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate

data class DtoInstitusjon(
    val institusjonsnavn: String,
    val periodeFom: LocalDate,
    val periodeTom: LocalDate
)
