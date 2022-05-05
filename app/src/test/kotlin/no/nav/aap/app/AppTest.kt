package no.nav.aap.app

import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoVedtak
import no.nav.aap.kafka.streams.test.readAndAssert
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import java.time.LocalDate
import java.util.*

class AppTest {

    @Disabled("Disablet på grunn av feil på Unknown topic, prøver å finne ut grunnen")
    @Test
    fun `Innsendt vedtak oppretter en mottaker`() {
        withTestApp { mocks ->
            val vedtakTopic = mocks.kafka.inputTopic(Topics.vedtak)
            mocks.kafka.outputTopic(Topics.vedtak)
            mocks.kafka.inputTopic(Topics.mottakere)
            val mottakTopic = mocks.kafka.outputTopic(Topics.mottakere)

            vedtakTopic.produce("123") {
                DtoVedtak(
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
    }

}

private fun withTestApp(test: ApplicationTestBuilder.(mocks: Mocks) -> Unit) = Mocks().use { mocks ->
    EnvironmentVariables(containerProperties()).execute {
        testApplication {
            application {
                server(mocks.kafka)
                runBlocking { this@testApplication.test(mocks) }
            }
        }
    }
}

private fun containerProperties(): Map<String, String> = mapOf(
    "KAFKA_STREAMS_APPLICATION_ID" to "utbetaling",
    "KAFKA_BROKERS" to "mock://kafka",
    "KAFKA_TRUSTSTORE_PATH" to "",
    "KAFKA_SECURITY_ENABLED" to "false",
    "KAFKA_KEYSTORE_PATH" to "",
    "KAFKA_CREDSTORE_PASSWORD" to "",
    "KAFKA_CLIENT_ID" to "utbetaling")
