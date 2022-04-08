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

}
