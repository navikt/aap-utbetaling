package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.hendelse.Lytter

sealed class DtoBehov {
    open fun erBarn() = false
    open fun erInstitusjon() = false

    open fun accept(lytter: Lytter) {}

    class DtoBehovBarn : DtoBehov() {
        override fun erBarn() = true
    }

    class DtoBehovInstitusjon : DtoBehov() {
        override fun erInstitusjon() = true
    }
}