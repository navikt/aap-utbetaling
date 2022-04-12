package no.nav.aap.domene.utbetaling.hendelse

open class Hendelse {
    private val behov = mutableListOf<Behov>()

    internal fun opprettBehov(behov: Behov) {
        this.behov.add(behov)
    }

    fun behov(): List<Behov> = behov.toList()
}

interface Behov {
    fun toDto(ident: String): DtoBehov
}

sealed class DtoBehov {
    open fun erBarn() = false
    open fun erInstitusjon() = false
    open fun erMeldeplikt() = false
    open fun erVedtak() = false

    open fun accept(lytter: Lytter) {}

    class Barn : DtoBehov() {
        override fun erBarn() = true
    }

    class Institusjon : DtoBehov() {
        override fun erInstitusjon() = true
    }

    class Meldeplikt : DtoBehov() {
        override fun erMeldeplikt() = true
    }

    class Vedtak : DtoBehov() {
        override fun erVedtak() = true
    }

}

interface Lytter {

}