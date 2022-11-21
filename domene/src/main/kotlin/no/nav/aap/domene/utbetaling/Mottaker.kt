package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApi
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import no.nav.aap.domene.utbetaling.hendelse.Hendelse
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningInstitusjon
import no.nav.aap.domene.utbetaling.observer.MottakerObserver
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjehistorikk
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor

internal class Mottaker private constructor(
    private val personident: Personident,
    private val fødselsdato: Fødselsdato,
    private val vedtakshistorikk: Vedtakshistorikk,
    private val aktivitetstidslinje: Aktivitetstidslinje,
    private val utbetalingstidslinjehistorikk: Utbetalingstidslinjehistorikk,
    private val barnetillegg: Barnetillegg,
    private val oppdragshistorikk: Oppdragshistorikk,
    private var tilstand: Tilstand = Tilstand.Start
) {
    private val observers = mutableListOf<MottakerObserver>()

    internal constructor(
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

    internal companion object {
        internal fun gjenopprett(mottakerModellApi: MottakerModellApi) = Mottaker(
            personident = Personident(mottakerModellApi.personident),
            fødselsdato = Fødselsdato(mottakerModellApi.fødselsdato),
            vedtakshistorikk = Vedtakshistorikk.gjenopprett(mottakerModellApi.vedtakshistorikk),
            aktivitetstidslinje = Aktivitetstidslinje.gjenopprett(mottakerModellApi.aktivitetstidslinje),
            utbetalingstidslinjehistorikk = Utbetalingstidslinjehistorikk.gjenopprett(mottakerModellApi.utbetalingstidslinjehistorikk),
            barnetillegg = Barnetillegg.gjenopprett(mottakerModellApi.barnetillegg),
            oppdragshistorikk = Oppdragshistorikk.gjenopprett(mottakerModellApi.oppdragshistorikk),
            tilstand = enumValueOf<Tilstand.Tilstandsnavn>(mottakerModellApi.tilstand).tilknyttetTilstand()
        )
    }

    internal fun registerObserver(observer: MottakerObserver) {
        observers.add(observer)
    }

    private fun notifyObservers(block: MottakerObserver.() -> Unit) {
        observers.forEach { it.block() }
    }

    internal fun håndterVedtak(vedtak: Vedtakshendelse) {
        tilstand.håndterVedtak(this, vedtak)
    }

    internal fun håndterMeldeplikt(melding: Meldepliktshendelse) {
        tilstand.håndterMeldeplikt(this, melding)
    }

    internal fun håndterLøsning(løsning: LøsningBarn) {
        tilstand.håndterLøsning(this, løsning)
    }

    internal fun håndterLøsning(løsningInstitusjon: LøsningInstitusjon) {

    }

    internal fun toDto() = MottakerModellApi(
        personident = personident.toDto(),
        fødselsdato = fødselsdato.toDto(),
        vedtakshistorikk = vedtakshistorikk.toDto(),
        aktivitetstidslinje = aktivitetstidslinje.toDto(),
        utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.toDto(),
        oppdragshistorikk = oppdragshistorikk.toDto(),
        barnetillegg = barnetillegg.toDto(),
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
                mottaker.notifyObservers { behovBarn() }
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
