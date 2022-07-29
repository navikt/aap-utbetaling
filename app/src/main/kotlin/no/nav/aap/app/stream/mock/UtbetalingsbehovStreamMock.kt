package no.nav.aap.app.stream.mock

import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoLøsning
import no.nav.aap.kafka.streams.extension.filterNotNull
import no.nav.aap.kafka.streams.extension.filterValues
import no.nav.aap.kafka.streams.extension.mapValues
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder


internal fun StreamsBuilder.utbetalingsbehovStreamMock() {
    mockConsume(Topics.utbetalingsbehov)
        .filterNotNull("mock-utbetalingsbehov-filter-tombstone")
        .filterValues("mock-utbetalingsbehov-filter-request") { løsning -> løsning.response == null }
        .filterValues("mock-utbetalingsbehov-filter-for-test-ident") { løsning -> løsning.request.ident != "123" }
        .mapValues("mock-utbetalingsbehov-opprett-response") { utbetalingsbehov ->
            utbetalingsbehov.copy(
                response = KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Response(
                    DtoLøsning(emptyList())
                )
            )
        }
        .produce(Topics.utbetalingsbehov, "mock-utbetalingsbehov-produced-response")
}
