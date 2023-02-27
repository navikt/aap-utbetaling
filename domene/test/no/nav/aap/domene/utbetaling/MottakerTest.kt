package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.entitet.*
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.hendelse.BrukeraktivitetPerDag
import no.nav.aap.domene.utbetaling.hendelse.Meldepliktshendelse
import no.nav.aap.domene.utbetaling.hendelse.Vedtakshendelse
import no.nav.aap.domene.utbetaling.hendelse.løsning.LøsningBarn
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Endringskode
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Fagområde
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Oppdragstatus
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinje
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Utbetalingstidslinjedag
import no.nav.aap.domene.utbetaling.visitor.MottakerVisitor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class MottakerTest {

    @Test
    fun `Nytt vedtak oppdaterer vedtakshistorikk`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))
        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        assertEquals(1, mottaker.inspektør.vedtakListeSize)
    }

    @Test
    fun `Nytt vedtak setter gjeldende vedtak i vedtakshistorikk`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))
        val vedtak1 = Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = 2 mai 2022,
            virkningsdato = 2 mai 2022,
            fødselsdato = Fødselsdato(30 november 1979)
        )
        mottaker.håndterVedtak(vedtak1)
        assertEquals(Vedtak.opprettFraVedtakshendelse(vedtak1), mottaker.inspektør.gjeldendeVedtak)

        val vedtak2 = Vedtakshendelse(
            vedtaksid = UUID.randomUUID(),
            innvilget = true,
            grunnlagsfaktor = Grunnlagsfaktor(3),
            vedtaksdato = 4 mai 2022,
            virkningsdato = 4 mai 2022,
            fødselsdato = Fødselsdato(30 november 1979)
        )
        mottaker.håndterVedtak(vedtak2)
        assertEquals(Vedtak.opprettFraVedtakshendelse(vedtak2), mottaker.inspektør.gjeldendeVedtak)
    }

    @Test
    fun `uavhengige innmeldte brukeraktiviteter aggregeres`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false))
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false))
            )
        )

        assertEquals(2, mottaker.inspektør.antallDagerIAktivitetstidslinje)
    }

    @Test
    fun `Flere uavhengige innmeldte brukeraktiviteter aggregeres`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false)
                )
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(BrukeraktivitetPerDag(4 mai 2022, 0.arbeidstimer, false))
            )
        )

        assertEquals(3, mottaker.inspektør.antallDagerIAktivitetstidslinje)
    }

    @Test
    fun `Utbetalingsdager beregner beløp ved håndtering av barnetillegg`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(3),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false)
                )
            )
        )

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(2, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(2, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
    }

    @Test
    fun `Utbetalingsdager lager oppdrag ved håndtering av barnetillegg`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false)
                )
            )
        )

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(2, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(2, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2318, mottaker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Utbetalingsdager med to fraværsdager trekkes`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(9272, mottaker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Utbetalingsdager med arbeid i helg gir 60 prosent utbetaling`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(10, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        //TODO: Skal barnetillegg reduseres tilsvarende arbeidsprosenten?
        assertEquals(6950, mottaker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Fyller 25 midt i meldeperioden, grunnlag skal justeres opp`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(8 mai 1997))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(1),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(8 mai 1997)
            )
        )
        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false), // Mandag
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false),
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

        mottaker.håndterLøsning(LøsningBarn(listOf()))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(10, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(0, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(7150, mottaker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Endringsvedtak om endret grunnlag endrer utbetaling`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(9272, mottaker.inspektør.totalBeløp[0])

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(5),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[1])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[1])
        assertEquals(11536, mottaker.inspektør.totalBeløp[1])
    }

    @Test
    fun `Ny meldepliktshendelse mottatt etter første beregning - trigger ikke ny beregning fordi venter på løsning`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(9272, mottaker.inspektør.totalBeløp[0])

        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(16 mai 2022, 0.arbeidstimer, true), // Mandag
                    BrukeraktivitetPerDag(17 mai 2022, 0.arbeidstimer, true),
                    BrukeraktivitetPerDag(18 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(19 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(20 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(21 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(22 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(23 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(24 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(25 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(26 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(27 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(28 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(29 mai 2022, 0.arbeidstimer, false),
                )
            )
        )

        assertEquals(28, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertNull(mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[1])
        assertNull(mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[1])
        assertNull(mottaker.inspektør.totalBeløp.getOrNull(1))
    }

    @Test
    fun `Ny meldepliktshendelse og løsning mottatt etter første beregning - trigger ny beregning`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(9272, mottaker.inspektør.totalBeløp[0])

        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(16 mai 2022, 0.arbeidstimer, true), // Mandag
                    BrukeraktivitetPerDag(17 mai 2022, 0.arbeidstimer, true),
                    BrukeraktivitetPerDag(18 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(19 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(20 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(21 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(22 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(23 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(24 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(25 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(26 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(27 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(28 mai 2022, 0.arbeidstimer, false),
                    BrukeraktivitetPerDag(29 mai 2022, 0.arbeidstimer, false),
                )
            )
        )

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(28, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(16, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[1])
        assertEquals(4, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[1])
        assertEquals(18544, mottaker.inspektør.totalBeløp[1])
    }

    @Test
    fun `Endringsvedtak om endret grunnlag etter meldepliktshendelse, men før løsning er mottatt`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(5),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(11536, mottaker.inspektør.totalBeløp[0])
    }

    @Test
    fun `Endring av tidligere innsendt meldeplikt trigger ny beregning`() {
        val mottaker = Mottaker(Personident("12345678910"), Fødselsdato(30 november 1979))

        mottaker.håndterVedtak(
            Vedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = Grunnlagsfaktor(4),
                vedtaksdato = 2 mai 2022,
                virkningsdato = 2 mai 2022,
                fødselsdato = Fødselsdato(30 november 1979)
            )
        )
        mottaker.håndterMeldeplikt(
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(8, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(2, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[0])
        assertEquals(9272, mottaker.inspektør.totalBeløp[0])


        mottaker.håndterMeldeplikt(
            Meldepliktshendelse(
                brukersAktivitet = listOf(
                    BrukeraktivitetPerDag(2 mai 2022, 0.arbeidstimer, false), // Mandag
                    BrukeraktivitetPerDag(3 mai 2022, 0.arbeidstimer, false),
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

        mottaker.håndterLøsning(LøsningBarn(listOf(Barnetillegg.Barn(Fødselsdato(5 juni 2010)))))

        assertEquals(14, mottaker.inspektør.antallDagerIAktivitetstidslinje)
        assertEquals(10, mottaker.inspektør.antallUtbetalingsdagerIUtbetalingstidslinje[1])
        assertEquals(0, mottaker.inspektør.antallIkkeUtbetalingsdagerIUtbetalingstidslinje[1])
        assertEquals(11590, mottaker.inspektør.totalBeløp[1])
    }

    private val Mottaker.inspektør get() = TestVisitor(this)

    private class TestVisitor(mottaker: Mottaker) : MottakerVisitor {

        var vedtakListeSize: Int = -1
        var antallDagerIAktivitetstidslinje: Int = 0
        private var utbetalingstidslinjeIndex: Int = -1
        var antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje = mutableMapOf<Int, Int>()
        var antallUtbetalingsdagerIUtbetalingstidslinje = mutableMapOf<Int, Int>()
        var antallIkkeUtbetalingsdagerIUtbetalingstidslinje = mutableMapOf<Int, Int>()
        lateinit var gjeldendeVedtak: Vedtak
        val totalBeløp = mutableListOf<Int>()

        init {
            mottaker.accept(this)
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

        override fun preVisitUtbetalingstidslinje(tidslinje: Utbetalingstidslinje) {
            utbetalingstidslinjeIndex++
            antallUtbetalingsdagerUtenBeløpIUtbetalingstidslinje[utbetalingstidslinjeIndex] = 0
            antallUtbetalingsdagerIUtbetalingstidslinje[utbetalingstidslinjeIndex] = 0
            antallIkkeUtbetalingsdagerIUtbetalingstidslinje[utbetalingstidslinjeIndex] = 0
        }

        override fun visitUtbetaling(
            dag: Utbetalingstidslinjedag.Utbetalingsdag,
            dato: LocalDate,
            beløp: Beløp
        ) {
            antallUtbetalingsdagerIUtbetalingstidslinje.computeIfPresent(utbetalingstidslinjeIndex) { _, old -> old + 1 }
        }

        override fun visitIkkeUtbetaling(dag: Utbetalingstidslinjedag.IkkeUtbetalingsdag, dato: LocalDate) {
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
