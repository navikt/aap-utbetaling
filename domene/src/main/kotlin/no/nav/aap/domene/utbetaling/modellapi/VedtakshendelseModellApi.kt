package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import java.time.LocalDate
import java.util.*

data class VedtakshendelseModellApi(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
) {
    fun håndter(mottakerModellApi: MottakerModellApi): MottakerModellApi {
        val mottaker = Mottaker.gjenopprett(mottakerModellApi)
        mottaker.håndterVedtak(Vedtakshendelse.gjenopprett(this))
        return mottaker.toModellApi()
    }
}
