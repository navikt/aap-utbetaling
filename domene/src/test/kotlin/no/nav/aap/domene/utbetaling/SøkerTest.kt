package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Endringskode
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Fagområde
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdragstatus
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingsdag
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class SøkerTest {

    @Test
    fun `Nytt vedtak oppdaterer vedtakshistorikk`() {
        val søker = Søker()
        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        assertEquals(1, søker.inspektør.vedtakListeSize)
    }

    @Test
    fun `Nytt vedtak setter gjeldende vedtak i vedtakshistorikk`() {
        val søker = Søker()
        val vedtak1 = Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = 2 mai 2022,
            virkningsdato = 2 mai 2022
        )
        søker.håndterVedtak(vedtak1)
        assertEquals(Vedtak.opprettFraVedtakshendelse(vedtak1), søker.inspektør.gjeldendeVedtak)

        val vedtak2 = Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = 4 mai 2022,
            virkningsdato = 4 mai 2022
        )
        søker.håndterVedtak(vedtak2)
        assertEquals(Vedtak.opprettFraVedtakshendelse(vedtak2), søker.inspektør.gjeldendeVedtak)
    }

    @Test
    fun `Ny melding oppdaterer tidslinje`() {
        val søker = Søker()
        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false)
                )
            )
        )

        assertEquals(1, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(1, søker.inspektør.antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[0])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
    }

    @Test
    fun `uavhengige innmeldte brukeraktiviteter aggregeres`() {
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false))
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false))
            )
        )

        assertEquals(2, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(1, søker.inspektør.antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[0])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, søker.inspektør.antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[1])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[1])
    }

    @Test
    fun `Flere uavhengige innmeldte brukeraktiviteter aggregeres`() {
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false)
                )
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(4 mai 2022, 0.arbeidstimer, false))
            )
        )

        assertEquals(3, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(2, søker.inspektør.antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[0])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(3, søker.inspektør.antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[1])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[1])
    }

    @Test
    fun `Utbetalingsdager beregner beløp ved håndtering av barnetillegg`() {
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false)
                )
            )
        )

        søker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(2, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(2, søker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
    }

    @Test
    fun `Utbetalingsdager lager oppdrag ved håndtering av barnetillegg`() {
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false)
                )
            )
        )

        søker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(2, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(2, søker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(1674, søker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Utbetalingsdager med to fraværsdager trekkes`() {
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, true), // Mandag
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, true),
                    BrukeraktivitetPerDag(4 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(5 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(6 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(7 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(8 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(9 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(10 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(11 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(12 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(13 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(14 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(15 mai 2022, 0.arbeidstimer, false),
                )
            )
        )

        søker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, søker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(6696, søker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Utbetalingsdager med arbeid i helg gir 60 prosent utbetaling`() {
        val søker = Søker()

        søker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022
            )
        )
        søker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false), // Mandag
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(4 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(5 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(6 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(7 mai 2022, 7.5.arbeidstimer, false),
                    BrukeraktivitetPerDag(8 mai 2022, 7.5.arbeidstimer, false),
                    BrukeraktivitetPerDag(9 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(10 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(11 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(12 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(13 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(14 mai 2022, 7.5.arbeidstimer, false),
                    BrukeraktivitetPerDag(15 mai 2022, 7.5.arbeidstimer, false),
                )
            )
        )

        søker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, søker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(10, søker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, søker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(5020, søker.inspektør.totalBeløp[0])
    }

    private val Søker.inspektør get() = TestVisitor(this)

    private class TestVisitor(søker: Søker) : SøkerVisitor {

        var vedtakListeSize: Int = -1
        var antallDagerIAktivitetstidslinje: Int = 0
        private var utbetalingstidslinjeIndex: Int = -1
        var antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje = mutableMapOf<Int, Int>()
        var antallUtbetalingsdagerIUtbetalingstidslinje = mutableMapOf<Int, Int>()
        var antallIkkeUtbetalingsdagerIUtbetalingstidslinje = mutableMapOf<Int, Int>()
        lateinit var gjeldendeVedtak: Vedtak
        val totalBeløp = mutableListOf<Int>()

        init {
            søker.accept(this)
        }

        override fun visitVedtakshistorikk(vedtak: List<Vedtak>) {
            vedtakListeSize = vedtak.size
        }

        override fun visitVedtakshistorikk(gjeldendeVedtak: Vedtak) {
            this.gjeldendeVedtak = gjeldendeVedtak
        }

        override fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {
            antallDagerIAktivitetstidslinje++
        }

        override fun visitArbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) {
            antallDagerIAktivitetstidslinje++
        }

        override fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dato: LocalDate) {
            antallDagerIAktivitetstidslinje++
        }

        override fun visitVentedag(dato: LocalDate) {
            antallDagerIAktivitetstidslinje++
        }

        override fun preVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {
            utbetalingstidslinjeIndex++
            antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[utbetalingstidslinjeIndex] = 0
            antallUtbetalingsdagerIUtbetalingstidslinje[utbetalingstidslinjeIndex] = 0
            antallIkkeUtbetalingsdagerIUtbetalingstidslinje[utbetalingstidslinjeIndex] = 0
        }

        override fun visitUtbetaling(dag: Utbetalingsdag.Utbetaling, dato: LocalDate) {
            antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje.computeIfPresent(utbetalingstidslinjeIndex) { _, old -> old + 1 }
        }

        override fun visitUtbetalingMedBeløp(dag: Utbetalingsdag.Utbetaling, dato: LocalDate, beløp: Beløp) {
            antallUtbetalingsdagerIUtbetalingstidslinje.computeIfPresent(utbetalingstidslinjeIndex) { _, old -> old + 1 }
        }

        override fun visitIkkeUtbetaling(dag: Utbetalingsdag.IkkeUtbetaling, dato: LocalDate) {
            antallIkkeUtbetalingsdagerIUtbetalingstidslinje.computeIfPresent(utbetalingstidslinjeIndex) { _, old -> old + 1 }
        }

        override fun preVisitOppdrag(
            oppdrag: Oppdrag,
            fagområde: Fagområde,
            fagsystemId: String,
            mottaker: String,
            førstedato: LocalDate,
            sistedato: LocalDate,
            stønadsdager: Int,
            totalBeløp: Int,
            nettoBeløp: Int,
            tidsstempel: LocalDateTime,
            endringskode: Endringskode,
            avstemmingsnøkkel: Long?,
            status: Oppdragstatus?,
            overføringstidspunkt: LocalDateTime?
        ) {
            this.totalBeløp.add(totalBeløp)
        }
    }
}
