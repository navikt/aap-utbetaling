package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag.Companion.opprettBrukersAktivitet
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse

data class DtoMeldepliktshendelse(
    val aktivitetPerDag: List<DtoAkivitetPerDag>
) {
    fun opprettMeldepliktshendelse() = Meldepliktshendelse(
        brukersAktivitet = aktivitetPerDag.opprettBrukersAktivitet()
    )
}
