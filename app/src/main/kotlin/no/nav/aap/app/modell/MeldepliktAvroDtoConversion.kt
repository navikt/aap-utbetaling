package no.nav.aap.app.modell

import no.nav.aap.app.kafka.KafkaBrukersAktivitetPerDag
import no.nav.aap.app.kafka.KafkaMeldeplikt
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoMeldeplikt

fun KafkaMeldeplikt.toDto() = DtoMeldeplikt(
    aktivitetPerDag = this.aktivitetPerDag.map { it.toDto() }
)

fun KafkaBrukersAktivitetPerDag.toDto() = DtoAkivitetPerDag(
    dato = this.dato
)