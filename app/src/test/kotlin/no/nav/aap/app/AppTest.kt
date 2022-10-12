package no.nav.aap.app

import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoLøsning
import no.nav.aap.domene.utbetaling.dto.DtoLøsningBarn
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

internal class AppTest {

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
