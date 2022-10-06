package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag.Companion.opprettBrukersAktivitet
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.observer.MottakerObserver

data class DtoMeldepliktshendelse(
    val aktivitetPerDag: List<DtoAkivitetPerDag>
) {
    fun håndter(dtoMottaker: DtoMottaker, observer: DtoMottakerObserver): DtoMottaker {
        val mottaker = Mottaker.gjenopprett(dtoMottaker)
        mottaker.registerObserver(ObserverDelegate(observer))
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = aktivitetPerDag.opprettBrukersAktivitet()
            )
        )
        return mottaker.toDto()
    }
}

private class ObserverDelegate(private val observer: DtoMottakerObserver) : MottakerObserver {
    override fun behovBarn() {
        observer.behovBarn()
    }
}
