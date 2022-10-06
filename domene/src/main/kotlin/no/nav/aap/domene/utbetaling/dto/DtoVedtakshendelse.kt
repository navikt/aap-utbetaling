package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import java.time.LocalDate
import java.util.*

data class DtoVedtakshendelse(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
) {
    fun håndter(dtoMottaker: DtoMottaker): DtoMottaker {
        val mottaker = Mottaker.gjenopprett(dtoMottaker)
        mottaker.håndterVedtak(Vedtakshendelse.gjenopprett(this))
        return mottaker.toDto()
    }
}
