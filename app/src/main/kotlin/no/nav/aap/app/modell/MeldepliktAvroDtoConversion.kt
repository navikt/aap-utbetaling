package no.nav.aap.app.modell

import no.nav.aap.app.kafka.AvroBrukersAktivitetPerDag
import no.nav.aap.app.kafka.AvroMeldeplikt
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoMeldeplikt

fun AvroMeldeplikt.toDto() = DtoMeldeplikt(
    aktivitetPerDag = this.aktivitetPerDag.map { it.toDto() }
)

fun AvroBrukersAktivitetPerDag.toDto() = DtoAkivitetPerDag(
    dato = this.dato
)