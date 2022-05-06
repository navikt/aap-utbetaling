package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Aktivitetstidslinje
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningInstitusjon
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjehistorikk
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor
import java.time.LocalDate

class Mottaker {
    private val aktivitetstidslinje = Aktivitetstidslinje()
    private val utbetalingstidslinjehistorikk = Utbetalingstidslinjehistorikk()
    private val vedtakshistorikk = Vedtakshistorikk()
    private val barnetillegg = Barnetillegg()
    private val oppdragshistorikk = Oppdragshistorikk()

    private var tilstand: Tilstand = Tilstand.Start

    companion object {
        fun gjenopprett(dtoMottaker: DtoMottaker) = Mottaker()
    }

    fun håndterVedtak(vedtak: Vedtakshendelse) {
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

    fun toDto() = DtoMottaker(
        personident = "123",
        fødselsdato = LocalDate.now()
    )

    private fun beregn() {
        val builder = vedtakshistorikk.utbetalingstidslinjeBuilder(barnetillegg)
        val utbetalingstidslinje = builder.build(aktivitetstidslinje)
        utbetalingstidslinjehistorikk.add(utbetalingstidslinje)
        utbetalingstidslinjehistorikk.barnetillegg(barnetillegg)
        utbetalingstidslinjehistorikk.byggOppdrag(oppdragshistorikk)
    }

    private sealed interface Tilstand {
        fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {}
        fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {}
        fun håndterLøsning(mottaker: Mottaker, løsning: LøsningBarn) {}

        object Start : Tilstand {
            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
                mottaker.tilstand = VedtakMottatt
            }
        }

        object VedtakMottatt : Tilstand {

            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
            }

            override fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {
                mottaker.aktivitetstidslinje.håndterMeldepliktshendelse(melding)
                mottaker.tilstand = MeldepliktshendelseMottatt
            }
        }

        object MeldepliktshendelseMottatt : Tilstand {

            //TODO on entry: behov -> slå opp barn og institusjon

            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
            }

            override fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {
                mottaker.aktivitetstidslinje.håndterMeldepliktshendelse(melding)
            }

            override fun håndterLøsning(mottaker: Mottaker, løsning: LøsningBarn) {
                løsning.leggTilBarn(mottaker.barnetillegg)

                mottaker.beregn()

                mottaker.tilstand = SisteKompletteGreie
            }
        }

        object SisteKompletteGreie : Tilstand { //FIXME: Trenger et bedre navn

            override fun håndterVedtak(mottaker: Mottaker, vedtak: Vedtakshendelse) {
                mottaker.vedtakshistorikk.leggTilNyttVedtak(vedtak)
                mottaker.beregn()
            }

            override fun håndterMeldeplikt(mottaker: Mottaker, melding: Meldepliktshendelse) {
                mottaker.aktivitetstidslinje.håndterMeldepliktshendelse(melding)
                mottaker.tilstand = MeldepliktshendelseMottatt
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
