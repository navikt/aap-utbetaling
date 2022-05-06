package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import java.time.LocalDate

data class DtoAkivitetPerDag(
    val dato: LocalDate,
    val arbeidstimer: Double,
    val fraværsdag: Boolean
) {
    companion object {
        internal fun Iterable<DtoAkivitetPerDag>.opprettBrukersAktivitet() = map {
            BrukeraktivitetPerDag(
                dato = it.dato,
                arbeidstimer = Arbeidstimer(it.arbeidstimer),
                fraværsdag = it.fraværsdag
            )
        }
    }
}
