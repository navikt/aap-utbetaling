package no.nav.aap.app.modell

import no.nav.aap.app.kafka.KafkaBarn
import no.nav.aap.app.kafka.KafkaBarna
import no.nav.aap.domene.utbetaling.dto.DtoBarn
import no.nav.aap.domene.utbetaling.dto.DtoBarna

fun KafkaBarna.toDto() = DtoBarna(
    barn = this.barn.map { it.toDto() }
)

fun KafkaBarn.toDto() = DtoBarn(
    fødselsnummer = this.fødselsnummer,
    fødselsdato = this.fødselsdato
)
