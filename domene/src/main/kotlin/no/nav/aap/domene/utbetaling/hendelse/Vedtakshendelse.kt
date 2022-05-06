package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.dto.DtoVedtakshendelse
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.LocalDate
import java.util.*

class Vedtakshendelse(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Grunnlagsfaktor,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: Fødselsdato
) : Hendelse() {
    companion object {
        fun gjenopprett(dtoVedtak: DtoVedtakshendelse) = Vedtakshendelse(
            vedtaksid = dtoVedtak.vedtaksid,
            innvilget = dtoVedtak.innvilget,
            grunnlagsfaktor = Grunnlagsfaktor(dtoVedtak.grunnlagsfaktor),
            vedtaksdato = dtoVedtak.vedtaksdato,
            virkningsdato = dtoVedtak.virkningsdato,
            fødselsdato = Fødselsdato(dtoVedtak.fødselsdato)
        )
    }
}