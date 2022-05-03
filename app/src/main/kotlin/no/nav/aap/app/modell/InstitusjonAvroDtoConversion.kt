package no.nav.aap.app.modell

import no.nav.aap.app.kafka.KafkaInstitusjon
import no.nav.aap.domene.utbetaling.dto.DtoInstitusjon

fun KafkaInstitusjon.toDto() = DtoInstitusjon(
    institusjonsnavn = this.institusjonsnavn,
    periodeFom = this.periodeFom,
    periodeTom = this.periodeTom
)