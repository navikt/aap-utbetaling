package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.hendelse.behov.Behov

open class Hendelse {
    private val behov = mutableListOf<Behov>()

    internal fun opprettBehov(behov: Behov) {
        this.behov.add(behov)
    }

    fun behov(): List<Behov> = behov.toList()
}

interface Lytter {

}