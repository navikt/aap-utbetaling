package app

import kafka.KafkaUtbetalingsbehovWrapper
import kafka.Topics
import no.nav.aap.domene.utbetaling.modellapi.AkivitetPerDagModellApi
import no.nav.aap.domene.utbetaling.modellapi.LøsningBarnModellApi
import no.nav.aap.domene.utbetaling.modellapi.LøsningModellApi
import no.nav.aap.domene.utbetaling.modellapi.MeldepliktshendelseModellApi
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import org.junit.jupiter.api.Test
import simulering.SimuleringRequest
import java.time.LocalDate
import java.util.*

internal class AppTest {

//    @Test
//    @Disabled("Disabler til vi finner ut av hvorfor denne ikke rydder skikkelig opp og får alle andre tester til å feile")
//    fun `test simulering`() {
//        MockEnvironment().use { mocks ->
//            testApplication {
//                environment { config = mocks.containerProperties }
//                application {
//                    server(mocks.kafka)
//                }
//
//                val client = createClient {
//                    install(ContentNegotiation) {
//                        jackson {
//                            registerModule(JavaTimeModule())
//                            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                        }
//                    }
//                }
//                // Hvis dette kallet kommenteres ut, så funker det
//                val response = client.post("/simuler/12345678910") {
//                    contentType(ContentType.Application.Json)
//                    setBody(
//                        SimuleringRequest(
//                            fødselsdato = LocalDate.now(),
//                            innvilget = false,
//                            grunnlagsfaktor = 20.0,
//                            vedtaksdato = LocalDate.now(),
//                            virkningsdato = LocalDate.now(),
//                            aktivitetsdager = enkelMeldeplikt()
//                        )
//                    )
//                }
//                val body: SimuleringResponse = response.body()
//                assertEquals(14, body.aktivitetstidslinje.size)
//                assertEquals(10, body.utbetalingstidslinje.size)
//                assertEquals(HttpStatusCode.OK, response.status)
//            }
//        }
//    }

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
            it.mottakereKafkaDto.personident == "123"
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
            it.mottakereKafkaDto.vedtakshistorikk.size == 1 && it.mottakereKafkaDto.vedtakshistorikk[0].vedtaksid == vedtaksid
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
            MeldepliktshendelseModellApi(
                aktivitetPerDag = listOf(
                    AkivitetPerDagModellApi(
                        dato = LocalDate.now(),
                        arbeidstimer = 0.0,
                        fraværsdag = true
                    )
                )
            )
        }

        mottakTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.mottakereKafkaDto.aktivitetstidslinje.size == 1
        }
    }

    @Test
    fun `Innsendt løsning beregner en utbetaling`() = withTestApp { mocks ->
        val vedtakTopic = mocks.kafka.testTopic(Topics.vedtak)
        val mottakTopic = mocks.kafka.testTopic(Topics.mottakere)
        val meldepliktTopic = mocks.kafka.testTopic(Topics.meldeplikt)
        val løsningTopic = mocks.kafka.testTopic(Topics.utbetalingsbehov)
        val løsningOutputTopic = mocks.kafka.testTopic(Topics.utbetalingsbehov)
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
            MeldepliktshendelseModellApi(
                aktivitetPerDag = listOf(
                    AkivitetPerDagModellApi(
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
                    LøsningModellApi(
                        barn = listOf(LøsningBarnModellApi(LocalDate.now()))
                    )
                )
            )
        }

        mottakTopic.assertThat().hasValuesForPredicate("123", 1) {
            it.mottakereKafkaDto.utbetalingstidslinjehistorikk.size == 1 && it.mottakereKafkaDto.oppdragshistorikk.size == 1
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
            MeldepliktshendelseModellApi(
                aktivitetPerDag = listOf(
                    AkivitetPerDagModellApi(
                        dato = LocalDate.now(),
                        arbeidstimer = 0.0,
                        fraværsdag = true
                    )
                )
            )
        }

        utbetalingsbehovOutputTopic.assertThat().hasLastValueMatching {
            it?.request != null && it.response == null
        }
    }

    private fun enkelMeldeplikt() = (0L..13).map {
        SimuleringRequest.AktivitetDag(
            dato = LocalDate.now().plusDays(it),
            arbeidstimer = 0.0,
            fraværsdag = false
        )
    }
}
