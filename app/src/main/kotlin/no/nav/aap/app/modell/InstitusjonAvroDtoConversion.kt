package no.nav.aap.app.modell

import no.nav.aap.app.kafka.AvroInstitusjon
import no.nav.aap.domene.utbetaling.dto.DtoInstitusjon

fun AvroInstitusjon.toDto() = DtoInstitusjon(
    institusjonsnavn = this.institusjonsnavn,
    periodeFom = this.periodeFom,
    periodeTom = this.periodeTom
)