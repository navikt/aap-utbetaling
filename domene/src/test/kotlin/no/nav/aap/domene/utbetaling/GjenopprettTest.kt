package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.erHelg
import no.nav.aap.domene.utbetaling.modellapi.*
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Paragraf_11_20_1_ledd_ModellAPI
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Paragraf_11_20_2_ledd_2_punktum_ModellAPI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class GjenopprettTest {

    @Test
    fun `Gjennopprett og lagre tom mottaker`() {
        val mottakerModellApi = MottakerModellApi(
            personident = "12345678910",
            fødselsdato = 1 januar 2000,
            vedtakshistorikk = emptyList(),
            aktivitetstidslinje = emptyList(),
            utbetalingstidslinjehistorikk = emptyList(),
            oppdragshistorikk = emptyList(),
            barnetillegg = emptyList(),
            tilstand = "START",
        )

        val mottakerFraDtoMottaker = Mottaker.gjenopprett(mottakerModellApi)

        val dtoMottakerFraMottaker = mottakerFraDtoMottaker.toDto()

        assertEquals(mottakerModellApi, dtoMottakerFraMottaker)
    }

    @Test
    fun `Gjennopprett og lagre mottaker med ett vedtak`() {
        val mottakerModellApi = MottakerModellApi(
            personident = "12345678910",
            fødselsdato = 1 januar 2000,
            vedtakshistorikk = listOf(
                VedtakModellApi(
                    vedtaksid = UUID.randomUUID(),
                    innvilget = true,
                    grunnlagsfaktor = 4.0,
                    vedtaksdato = 10 oktober 2022,
                    virkningsdato = 10 oktober 2022,
                    fødselsdato = 1 januar 2000,
                )
            ),
            aktivitetstidslinje = emptyList(),
            utbetalingstidslinjehistorikk = emptyList(),
            oppdragshistorikk = emptyList(),
            barnetillegg = emptyList(),
            tilstand = "VEDTAK_MOTTATT",
        )

        val mottakerFraDtoMottaker = Mottaker.gjenopprett(mottakerModellApi)

        val dtoMottakerFraMottaker = mottakerFraDtoMottaker.toDto()

        assertEquals(mottakerModellApi, dtoMottakerFraMottaker)
    }

    @Test
    fun `Gjennopprett og lagre mottaker med ett vedtak og meldeplikthendelse`() {
        val mottakerModellApi = MottakerModellApi(
            personident = "12345678910",
            fødselsdato = 1 januar 2000,
            vedtakshistorikk = listOf(
                VedtakModellApi(
                    vedtaksid = UUID.randomUUID(),
                    innvilget = true,
                    grunnlagsfaktor = 4.0,
                    vedtaksdato = 10 oktober 2022,
                    virkningsdato = 10 oktober 2022,
                    fødselsdato = 1 januar 2000,
                )
            ),
            aktivitetstidslinje = listOf(
                MeldeperiodeModellApi(dager =
                (0 until 14L).map { nummer ->
                    val dato = (10 oktober 2022).plusDays(nummer)
                    DagModellApi(
                        dato = dato,
                        arbeidstimer = 0.0,
                        type = if (dato.erHelg()) "HELG" else "ARBEIDSDAG",
                    )
                })
            ),
            utbetalingstidslinjehistorikk = emptyList(),
            oppdragshistorikk = emptyList(),
            barnetillegg = emptyList(),
            tilstand = "MELDEPLIKTSHENDELSE_MOTTATT",
        )

        val mottakerFraDtoMottaker = Mottaker.gjenopprett(mottakerModellApi)

        val dtoMottakerFraMottaker = mottakerFraDtoMottaker.toDto()

        assertEquals(mottakerModellApi, dtoMottakerFraMottaker)
    }

    @Test
    fun `Gjennopprett og lagre mottaker med barnetillegg`() {
        val mottakerModellApi = MottakerModellApi(
            personident = "12345678910",
            fødselsdato = 1 januar 2000,
            vedtakshistorikk = emptyList(),
            aktivitetstidslinje = emptyList(),
            utbetalingstidslinjehistorikk = emptyList(),
            oppdragshistorikk = emptyList(),
            barnetillegg = listOf(
                BarnaModellApi(
                    barn = listOf(
                        BarnModellApi(
                            fødselsdato = 1 januar 2022
                        )
                    )
                )
            ),
            tilstand = "MELDEPLIKTSHENDELSE_MOTTATT",
        )

        val mottakerFraDtoMottaker = Mottaker.gjenopprett(mottakerModellApi)

        val dtoMottakerFraMottaker = mottakerFraDtoMottaker.toDto()

        assertEquals(mottakerModellApi, dtoMottakerFraMottaker)
    }

    @Test
    fun `Gjennopprett og lagre mottaker med beregnet utbetaling`() {
        val mottakerModellApi = MottakerModellApi(
            personident = "12345678910",
            fødselsdato = 1 januar 2000,
            vedtakshistorikk = listOf(
                VedtakModellApi(
                    vedtaksid = UUID.randomUUID(),
                    innvilget = true,
                    grunnlagsfaktor = 4.0,
                    vedtaksdato = 10 oktober 2022,
                    virkningsdato = 10 oktober 2022,
                    fødselsdato = 1 januar 2000,
                )
            ),
            aktivitetstidslinje = listOf(
                MeldeperiodeModellApi(dager =
                (0 until 14L).map { nummer ->
                    val dato = (10 oktober 2022).plusDays(nummer)
                    DagModellApi(
                        dato = dato,
                        arbeidstimer = 0.0,
                        type = if (dato.erHelg()) "HELG" else "ARBEIDSDAG",
                    )
                })
            ),
            utbetalingstidslinjehistorikk = listOf(UtbetalingstidslinjeModellApi(dager = (0 until 14L).mapNotNull { nummer ->
                val dato = (10 oktober 2022).plusDays(nummer)
                if (dato.erHelg()) return@mapNotNull null
                UtbetalingstidslinjedagModellApi(
                    type = "UTBETALINGSDAG",
                    dato = dato,
                    grunnlagsfaktor = 4.0,
                    barnetillegg = 0.0,
                    grunnlag = 445908.0,
                    årligYtelse = Paragraf_11_20_1_ledd_ModellAPI(0.66, 445908.0, 294299.28),
                    dagsats = Paragraf_11_20_2_ledd_2_punktum_ModellAPI(260, 294299.28, 1131.92),
                    høyesteÅrligYtelseMedBarnetillegg = 401317.2,
                    høyesteBeløpMedBarnetillegg = 1543.53,
                    dagsatsMedBarnetillegg = 1131.92,
                    beløpMedBarnetillegg = 1131.92,
                    beløp = 1131.92,
                    arbeidsprosent = 0.0,
                )
            })),
            oppdragshistorikk = listOf(
                OppdragModellApi(
                    mottaker = "12345678910",
                    fagområde = "Arbeidsavklaringspenger",
                    linjer = listOf(
                        UtbetalingslinjeModellApi(
                            fom = 10 oktober 2022,
                            tom = 23 oktober 2022,
                            satstype = "DAG",
                            beløp = 1132,
                            aktuellDagsinntekt = 1132,
                            grad = 100,
                            refFagsystemId = "OMRLRHMGSNBFBI4YQU3MF54CFI",
                            delytelseId = 1,
                            refDelytelseId = null,
                            endringskode = "NY",
                            klassekode = "AAP",
                            datoStatusFom = null,
                        )
                    ),
                    fagsystemId = "OMRLRHMGSNBFBI4YQU3MF54CFI",
                    endringskode = "NY",
                    nettoBeløp = 11320,
                    overføringstidspunkt = (24 oktober 2022).atTime(12, 34),
                    avstemmingsnøkkel = 1,
                    status = "AKSEPTERT",
                    tidsstempel = (24 oktober 2022).atTime(12, 34),
                )
            ),
            barnetillegg = emptyList(),
            tilstand = "UTBETALING_BEREGNET",
        )

        val mottakerFraDtoMottaker = Mottaker.gjenopprett(mottakerModellApi)

        val dtoMottakerFraMottaker = mottakerFraDtoMottaker.toDto()

        assertEquals(mottakerModellApi, dtoMottakerFraMottaker)
    }
}
