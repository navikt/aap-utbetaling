package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import java.time.LocalDate

data class AkivitetPerDagModellApi(
    val dato: LocalDate,
    val arbeidstimer: Double,
    val fraværsdag: Boolean
) {
    companion object {
        internal fun Iterable<AkivitetPerDagModellApi>.opprettBrukersAktivitet() = map {
            BrukeraktivitetPerDag(
                dato = it.dato,
                arbeidstimer = Arbeidstimer(it.arbeidstimer),
                fraværsdag = it.fraværsdag
            )
        }
    }
}
