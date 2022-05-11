package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.hendelse.Lytter

interface DtoBehov {
    fun erBarn() = false
    fun erInstitusjon() = false

    fun accept(lytter: Lytter) {}

    class DtoBehovBarn : DtoBehov {
        override fun erBarn() = true

        override fun accept(lytter: Lytter) {
            lytter.barn()
        }
    }

    class DtoBehovInstitusjon : DtoBehov {
        override fun erInstitusjon() = true
    }
}
