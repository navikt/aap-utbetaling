package no.nav.aap.domene.utbetaling.modellapi

import java.time.LocalDate

data class Paragraf_11_19_3_leddModellApi(
    val dato: LocalDate,
    val grunnlagsfaktor: Double,
    val grunnbeløp: Double,
    val grunnlag: Double
)
