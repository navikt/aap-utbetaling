package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.UtbetalingstidslinjeBuilder
import no.nav.aap.domene.utbetaling.modellapi.VedtakModellApi
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import java.time.LocalDate
import java.util.*

internal class Vedtak(
    private val vedtaksid: UUID,
    private val innvilget: Boolean,
    private val grunnlagsfaktor: Grunnlagsfaktor,
    private val vedtaksdato: LocalDate,
    private val virkningsdato: LocalDate,
    private val fødselsdato: Fødselsdato
) {

    internal companion object {
        internal fun opprettFraVedtakshendelse(vedtakshendelse: Vedtakshendelse) = Vedtak(
            vedtaksid = vedtakshendelse.vedtaksid,
            innvilget = vedtakshendelse.innvilget,
            grunnlagsfaktor = vedtakshendelse.grunnlagsfaktor,
            vedtaksdato = vedtakshendelse.vedtaksdato,
            virkningsdato = vedtakshendelse.virkningsdato,
            fødselsdato = vedtakshendelse.fødselsdato
        )

        internal fun gjenopprett(vedtakModellApi: VedtakModellApi) = Vedtak(
            vedtaksid = vedtakModellApi.vedtaksid,
            innvilget = vedtakModellApi.innvilget,
            grunnlagsfaktor = Grunnlagsfaktor(vedtakModellApi.grunnlagsfaktor),
            vedtaksdato = vedtakModellApi.vedtaksdato,
            virkningsdato = vedtakModellApi.virkningsdato,
            fødselsdato = Fødselsdato(vedtakModellApi.fødselsdato)
        )

        internal fun Iterable<Vedtak>.sortertPåDato() = this.sortedBy { it.vedtaksdato }
    }

    internal fun utbetalingstidslinjeBuilder(barnetillegg: Barnetillegg) =
        UtbetalingstidslinjeBuilder(grunnlagsfaktor, fødselsdato, barnetillegg)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vedtak

        if (vedtaksid != other.vedtaksid) return false

        return true
    }

    override fun hashCode() = vedtaksid.hashCode()

    internal fun toDto() = VedtakModellApi(
        vedtaksid = vedtaksid,
        innvilget = innvilget,
        grunnlagsfaktor = grunnlagsfaktor.toDto(),
        vedtaksdato = vedtaksdato,
        virkningsdato = virkningsdato,
        fødselsdato = fødselsdato.toDto()
    )
}
