package no.nav.aap.app

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.simulering.SimuleringRequest
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoLøsning
import no.nav.aap.domene.utbetaling.dto.DtoLøsningBarn
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals

internal class AppTest {

    @Test
    @Disabled("Disabler til vi finner ut av hvorfor denne ikke rydder skikkelig opp")
    fun `test simulering`() {
        MockEnvironment().use { mocks ->
            testApplication {
                environment { config = mocks.containerProperties }
                application {
                    server(mocks.kafka)
                }

                val client = createClient {
                    install(ContentNegotiation) {
                        jackson {
                            registerModule(JavaTimeModule())
                            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        }
                    }
                }
                // Hvis dette kallet kommenteres ut, så funker det
                val response = client.post("/simuler/12345678910") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        SimuleringRequest(
                            fødselsdato = LocalDate.now(),
                            innvilget = false,
                            grunnlagsfaktor = 20.0,
                            vedtaksdato = LocalDate.now(),
                            virkningsdato = LocalDate.now()
                        )
                    )
                }
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }
    }

    @Test
    fun `Innsendt vedtak oppretter en mottaker`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.testTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.testTopic(Topics.mottakere)

        vedtakTopic.produce("123") {
            IverksettVedtakKafkaDto(
                vedtaksid = UUID.randomUUID(),
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        mottakTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.personident == "123"
        }
    }

    @Test
    fun `Innsendt vedtak oppretter en mottaker med vedtakshistorikk`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.testTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.testTopic(Topics.mottakere)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            IverksettVedtakKafkaDto(
                vedtaksid = vedtaksid,
                innvilget = true,
                grunnlagsfaktor = 3.0,
                vedtaksdato = LocalDate.now(),
                virkningsdato = LocalDate.now(),
                fødselsdato = LocalDate.now()
            )
        }

        mottakTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.vedtakshistorikk.size == 1 && it.vedtakshistorikk[0].vedtaksid == vedtaksid
        }
    }

    @Test
    fun `Innsendt meldepliktshendelse oppretter en periode i aktivitetstidslinja`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.testTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.testTopic(Topics.mottakere)
        val meldepliktTopic = mocks.kafka.testTopic(Topics.meldeplikt)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            IverksettVedtakKafkaDto(
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

        mottakTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.aktivitetstidslinje.size == 1
        }
    }

    @Test
    fun `Innsendt løsning beregner en utbetaling`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.testTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.testTopic(Topics.mottakere)
        val meldepliktTopic = mocks.kafka.testTopic(Topics.meldeplikt)
        val løsningTopic = mocks.kafka.testTopic(Topics.utbetalingsbehov)
        val løsningOutputTopic = mocks.kafka.outputTopic(Topics.utbetalingsbehov)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            IverksettVedtakKafkaDto(
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

        val behov = løsningOutputTopic.readValue()
        løsningTopic.produce("123") {
            behov.copy(
                response = KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Response(
                    DtoLøsning(
                        barn = listOf(DtoLøsningBarn(LocalDate.now()))
                    )
                )
            )
        }

        mottakTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.utbetalingstidslinjehistorikk.size == 1 && it.oppdragshistorikk.size == 1
        }
    }

    @Test
    fun `Meldepliktshendelse trigger behov for barn`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.testTopic(Topics.vedtak)
        val meldepliktTopic = mocks.kafka.testTopic(Topics.meldeplikt)
        val utbetalingsbehovOutputTopic = mocks.kafka.testTopic(Topics.utbetalingsbehov)
        val vedtaksid = UUID.randomUUID()
        vedtakTopic.produce("123") {
            IverksettVedtakKafkaDto(
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

        utbetalingsbehovOutputTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.request.ident == "123"
        }
    }
}
