package no.nav.aap.domene.utbetaling.hendelse.behov

import no.nav.aap.domene.utbetaling.dto.DtoBehov

interface Behov {
    fun toDto(ident: String): DtoBehov
}