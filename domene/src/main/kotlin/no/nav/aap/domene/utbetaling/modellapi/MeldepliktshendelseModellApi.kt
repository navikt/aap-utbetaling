package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.modellapi.AkivitetPerDagModellApi.Companion.opprettBrukersAktivitet
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.observer.MottakerObserver

data class MeldepliktshendelseModellApi(
    val aktivitetPerDag: List<AkivitetPerDagModellApi>
) {
    fun håndter(mottakerModellApi: MottakerModellApi, observer: MottakerModellApiObserver): MottakerModellApi {
        val mottaker = Mottaker.gjenopprett(mottakerModellApi)
        mottaker.registerObserver(ObserverDelegate(observer))
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = aktivitetPerDag.opprettBrukersAktivitet()
            )
        )
        return mottaker.toModellApi()
    }
}

private class ObserverDelegate(private val observer: MottakerModellApiObserver) : MottakerObserver {
    override fun behovBarn() {
        observer.behovBarn()
    }
}
