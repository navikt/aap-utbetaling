package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import no.nav.aap.domene.utbetaling.hendelse.Hendelse
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.behov.BehovBarn
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningInstitusjon
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjehistorikk
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

class Mottaker private constructor(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val vedtakshistorikk: Vedtakshistorikk,
    private val aktivitetstidslinje: Aktivitetstidslinje,
    private val utbetalingstidslinjehistorikk: Utbetalingstidslinjehistorikk,
    private val barnetillegg: Barnetillegg,
    private val oppdragshistorikk: Oppdragshistorikk,
    private var tilstand: Tilstand = Tilstand.Start
) {

    constructor(
        personident: Personident,
        fødselsdato: Fødselsdato
    ) : this(
        personident,
        fødselsdato,
        Vedtakshistorikk(),
        Aktivitetstidslinje(),
        Utbetalingstidslinjehistorikk(),
        Barnetillegg(),
        Oppdragshistorikk()
    )

    companion object {
        fun gjenopprett(dtoMottaker: DtoMottaker) = Mottaker(
            personident = Personident(dtoMottaker.personident),
            fødselsdato = Fødselsdato(dtoMottaker.fødselsdato),
            vedtakshistorikk = Vedtakshistorikk.gjenopprett(dtoMottaker.vedtakshistorikk),
            aktivitetstidslinje = Aktivitetstidslinje.gjenopprett(dtoMottaker.aktivitetstidslinje),
            utbetalingstidslinjehistorikk = Utbetalingstidslinjehistorikk(),
            barnetillegg = Barnetillegg(),
            oppdragshistorikk = Oppdragshistorikk(),
            tilstand = enumValueOf<Tilstand.Tilstandsnavn>(dtoMottaker.tilstand).tilknyttetTilstand()
        )
    }

    fun håndterVedtak(vedtak: Vedtakshendelse) {
        tilstand.håndterVedtak(this, vedtak)
    }

    fun håndterMeldeplikt(melding: Meldepliktshendelse) {
        tilstand.håndterMeldeplikt(this, melding)
    }

    fun håndterLøsning(løsning: LøsningBarn) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsningInstitusjon: LøsningInstitusjon) {

    }

    fun toDto() = DtoMottaker(
        personident = personident.toDto(),
        fødselsdato = fødselsdato.toDto(),
        vedtakshistorikk = vedtakshistorikk.toDto(),
        aktivitetstidslinje = aktivitetstidslinje.toDto(),
        utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.toDto(),
        oppdragshistorikk = oppdragshistorikk.toDto(),
        tilstand = tilstand.toDto()
    )

    private fun beregn() {
        val builder = vedtakshistorikk.utbetalingstidslinjeBuilder(barnetillegg)
        val utbetalingstidslinje = builder.build(aktivitetstidslinje)
        utbetalingstidslinjehistorikk.add(utbetalingstidslinje)
        utbetalingstidslinjehistorikk.barnetillegg(barnetillegg)
        utbetalingstidslinjehistorikk.byggOppdrag(oppdragshistorikk)
    }

    private fun tilstand(hendelse: Hendelse, nyTilstand: Tilstand) {
        this.tilstand.leaving(this, hendelse)
        this.tilstand = nyTilstand
        this.tilstand.entering(this, hendelse)
    }

    private sealed class Tilstand(private val tilstandsnavn: Tilstandsnavn) {

        enum class Tilstandsnavn(internal val tilknyttetTilstand: () -> Tilstand) {
            START({ Start }),
            VEDTAK_MOTTATT({ VedtakMottatt }),
            MELDEPLIKTSHENDELSE_MOTTATT({ MeldepliktshendelseMottatt }),
            UTBETALING_BEREGNET({ UtbetalingBeregnet })
        }

        fun toDto() = tilstandsnavn.name

        open fun entering(mottaker: Mottaker, hendelse: Hendelse) {}
        open fun leaving(mottaker: Mottaker, hendelse: Hendelse) {}

        open fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {}
        open fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {}
        open fun håndterLøsning(mottaker: Mottaker, løsning: LøsningBarn) {}

        object Start : Tilstand(tilstandsnavn = Tilstandsnavn.START) {
            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
                mottaker.tilstand(vedtak, VedtakMottatt)
            }
        }

        object VedtakMottatt : Tilstand(tilstandsnavn = Tilstandsnavn.VEDTAK_MOTTATT) {

            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
            }

            override fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {
                mottaker.aktivitetstidslinje.håndterMeldepliktshendelse(melding)
                mottaker.tilstand(melding, MeldepliktshendelseMottatt)
            }
        }

        object MeldepliktshendelseMottatt : Tilstand(tilstandsnavn = Tilstandsnavn.MELDEPLIKTSHENDELSE_MOTTATT) {

            //TODO on entry: behov -> slå opp barn og institusjon
            override fun entering(mottaker: Mottaker, hendelse: Hendelse) {
                hendelse.opprettBehov(BehovBarn())
            }

            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
            }

            override fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {
                mottaker.aktivitetstidslinje.håndterMeldepliktshendelse(melding)
            }

            override fun håndterLøsning(mottaker: Mottaker, løsning: LøsningBarn) {
                løsning.leggTilBarn(mottaker.barnetillegg)

                mottaker.beregn()

                mottaker.tilstand(løsning, UtbetalingBeregnet)
            }
        }

        object UtbetalingBeregnet : Tilstand(tilstandsnavn = Tilstandsnavn.UTBETALING_BEREGNET) {

            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
                mottaker.beregn()
            }

            override fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {
                mottaker.aktivitetstidslinje.håndterMeldepliktshendelse(melding)
                mottaker.tilstand(melding, MeldepliktshendelseMottatt)
            }
        }
    }

    internal fun accept(visitor: MottakerVisitor) {
        aktivitetstidslinje.accept(visitor)
        utbetalingstidslinjehistorikk.accept(visitor)
        vedtakshistorikk.accept(visitor)
        oppdragshistorikk.accept(visitor)
    }
}
