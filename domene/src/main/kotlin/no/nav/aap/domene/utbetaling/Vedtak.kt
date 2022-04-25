package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import java.time.LocalDate
import java.util.*

internal class Vedtak(
    private val vedtaksid: UUID,
    private val innvilget: Boolean,
    private val grunnlagsfaktor: Grunnlagsfaktor,
    private val vedtaksdato: LocalDate,
    private val virkningsdato: LocalDate
) {

    internal companion object {
        internal fun opprettFraVedtakshendelse(vedtakshendelse: Vedtakshendelse) = Vedtak(
            vedtaksid = vedtakshendelse.vedtaksid,
            innvilget = vedtakshendelse.innvilget,
            grunnlagsfaktor = vedtakshendelse.grunnlagsfaktor,
            vedtaksdato = vedtakshendelse.vedtaksdato,
            virkningsdato = vedtakshendelse.virkningsdato
        )

        internal fun Iterable<Vedtak>.sortertPåDato() = this.sortedBy { it.vedtaksdato }
    }

    internal fun oppdaterTidslinje(aktivitetstidslinje: Aktivitetstidslinje, melding: Meldepliktshendelse) {
        aktivitetstidslinje.håndterMeldepliktshendelse(melding, grunnlagsfaktor, virkningsdato)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vedtak

        if (vedtaksid != other.vedtaksid) return false

        return true
    }

    override fun hashCode(): Int {
        return vedtaksid.hashCode()
    }
}