package no.nav.aap.app

import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.*
import no.nav.aap.kafka.streams.test.readAndAssert
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class AppTest {

    @Test
    fun `Innsendt vedtak oppretter en mottaker`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.inputTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.outputTopic(Topics.mottakere)

        vedtakTopic.produce("123") {
            DtoVedtakshendelse(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        mottakTopic.readAndAssert().hasValuesForPredicate("123", 1) {
            it.personident == "123"
        }
    }

    @Test
    fun `Innsendt vedtak oppretter en mottaker med vedtakshistorikk`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.inputTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.outputTopic(Topics.mottakere)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            DtoVedtakshendelse(
                vedtaksid = vedtaksid,
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        mottakTopic.readAndAssert().hasValuesForPredicate("123", 1) {
            it.vedtakshistorikk.size == 1 && it.vedtakshistorikk[0].vedtaksid == vedtaksid
        }
    }

    @Test
    fun `Innsendt meldepliktshendelse oppretter en periode i aktivitetstidslinja`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.inputTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.outputTopic(Topics.mottakere)
        val meldepliktTopic = mocks.kafka.inputTopic(Topics.meldeplikt)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            DtoVedtakshendelse(
                vedtaksid = vedtaksid,
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        meldepliktTopic.produce("123") {
            DtoMeldepliktshendelse(
                aktivitetPerDag = listOf(
                    DtoAkivitetPerDag(
                        dato = LocalDate.now(),
                        arbeidstimer = 0.0,
                        fraværsdag = true
                    )
                )
            )
        }

        mottakTopic.readAndAssert().hasValuesForPredicate("123", 1) {
            it.aktivitetstidslinje.size == 1
        }
    }

    @Test
    fun `Innsendt løsning beregner en utbetaling`() = withTestApp { mocks ->
        val vedtakInputTopic = mocks.kafka.inputTopic(Topics.vedtak)
        val mottakOutputTopic = mocks.kafka.outputTopic(Topics.mottakere)
        val meldepliktInputTopic = mocks.kafka.inputTopic(Topics.meldeplikt)
        val løsningInputTopic = mocks.kafka.inputTopic(Topics.utbetalingsbehov)
        val løsningOutputTopic = mocks.kafka.outputTopic(Topics.utbetalingsbehov)
        val vedtaksid = UUID.randomUUID()
        vedtakInputTopic.produce("123") {
            DtoVedtakshendelse(
                vedtaksid = vedtaksid,
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        meldepliktInputTopic.produce("123") {
            DtoMeldepliktshendelse(
                aktivitetPerDag = listOf(
                    DtoAkivitetPerDag(
                        dato = LocalDate.now(),
                        arbeidstimer = 0.0,
                        fraværsdag = true
                    )
                )
            )
        }

        val behov = løsningOutputTopic.readValue()
        løsningInputTopic.produce("123") {
            behov.copy(
                response = KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Response(
                    DtoLøsning(
                        barn = listOf(DtoLøsningBarn(LocalDate.now()))
                    )
                )
            )
        }

        mottakOutputTopic.readAndAssert().hasValuesForPredicate("123", 1) {
            it.utbetalingstidslinjehistorikk.size == 1 && it.oppdragshistorikk.size == 1
        }
    }

    @Test
    fun `Meldepliktshendelse trigger behov for barn`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.inputTopic(Topics.vedtak)
        val meldepliktTopic = mocks.kafka.inputTopic(Topics.meldeplikt)
        val utbetalingsbehovOutputTopic = mocks.kafka.outputTopic(Topics.utbetalingsbehov)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            DtoVedtakshendelse(
                vedtaksid = vedtaksid,
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        meldepliktTopic.produce("123") {
            DtoMeldepliktshendelse(
                aktivitetPerDag = listOf(
                    DtoAkivitetPerDag(
                        dato = LocalDate.now(),
                        arbeidstimer = 0.0,
                        fraværsdag = true
                    )
                )
            )
        }

        utbetalingsbehovOutputTopic.readAndAssert().hasValuesForPredicate("123", 1) {
            it.request.ident == "123"
        }
    }
}
