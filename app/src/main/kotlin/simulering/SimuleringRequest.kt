package simulering

import no.nav.aap.domene.utbetaling.modellapi.AkivitetPerDagModellApi
import no.nav.aap.domene.utbetaling.modellapi.MeldepliktshendelseModellApi
import no.nav.aap.domene.utbetaling.modellapi.VedtakshendelseModellApi
import java.time.LocalDate
import java.util.*

data class SimuleringRequest(
    val fødselsdato: LocalDate,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val aktivitetsdager: List<AktivitetDag>
) {
    data class AktivitetDag(
        val dato: LocalDate,
        val arbeidstimer: Double,
        val fraværsdag: Boolean
    )

    fun lagVedtakshendelse() = VedtakshendelseModellApi(
        vedtaksid = UUID.randomUUID(),
        fødselsdato = fødselsdato,
        innvilget = innvilget,
        grunnlagsfaktor = grunnlagsfaktor,
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato
    )

    fun lagMeldepliktshendelse() = MeldepliktshendelseModellApi(
        aktivitetPerDag = aktivitetsdager.map {
            AkivitetPerDagModellApi(
                dato = it.dato,
                arbeidstimer = it.arbeidstimer,
                fraværsdag = it.fraværsdag
            )
        }
    )
}