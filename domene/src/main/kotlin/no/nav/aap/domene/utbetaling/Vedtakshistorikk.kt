package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Vedtak.Companion.sortertPåDato
import no.nav.aap.domene.utbetaling.dto.DtoVedtak
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

internal class Vedtakshistorikk private constructor(
    private val vedtakshistorikk: MutableList<Vedtak>
) {
    internal constructor(): this(mutableListOf())

    companion object {
        fun gjenopprett(dtoVedtak: List<DtoVedtak>) = Vedtakshistorikk(
            vedtakshistorikk = dtoVedtak.map { Vedtak.gjenopprett(it) }.toMutableList()
        )
    }

    internal fun leggTilNyttVedtak(vedtakshendelse: Vedtakshendelse) {
        val vedtak = Vedtak.opprettFraVedtakshendelse(vedtakshendelse)
        vedtakshistorikk.add(vedtak)
    }

    private fun finnGjeldendeVedtak() = vedtakshistorikk.sortertPåDato().last()

    internal fun utbetalingstidslinjeBuilder(barnetillegg: Barnetillegg) =
        finnGjeldendeVedtak().utbetalingstidslinjeBuilder(barnetillegg)

    internal fun accept(visitor: MottakerVisitor) {
        visitor.visitVedtakshistorikk(vedtakshistorikk.toList())
        visitor.visitVedtakshistorikk(finnGjeldendeVedtak())
    }

    internal fun toDto(): List<DtoVedtak> = vedtakshistorikk.map { it.toDto() }

}