package no.nav.aap.app.modell

import no.nav.aap.app.kafka.AvroBarn
import no.nav.aap.app.kafka.AvroBarna
import no.nav.aap.domene.utbetaling.dto.DtoBarn
import no.nav.aap.domene.utbetaling.dto.DtoBarna

fun AvroBarna.toDto() = DtoBarna(
    barn = this.barn.map { it.toDto() }
)

fun AvroBarn.toDto() = DtoBarn(
    fødselsnummer = this.fødselsnummer,
    fødselsdato = this.fødselsdato
)
