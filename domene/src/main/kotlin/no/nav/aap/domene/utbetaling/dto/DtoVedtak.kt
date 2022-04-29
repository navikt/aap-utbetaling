package no.nav.aap.domene.utbetaling.dto

import no.nav.aap.domene.utbetaling.Søker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import java.time.LocalDate
import java.util.*

data class DtoVedtak(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
) {
    fun håndter(søker: Søker) {
        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = vedtaksid,
                innvilget = innvilget,
                grunnlagsfaktor = Grunnlagsfaktor(grunnlagsfaktor),
                vedtaksdato = vedtaksdato,
                virkningsdato = virkningsdato,
                fødselsdato = Fødselsdato(fødselsdato)
            )
        )
    }
}