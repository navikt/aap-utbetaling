package no.nav.aap.domene.utbetaling.modellapi

import java.time.LocalDate

data class InstitusjonModellApi(
    val institusjonsnavn: String,
    val periodeFom: LocalDate,
    val periodeTom: LocalDate
)
