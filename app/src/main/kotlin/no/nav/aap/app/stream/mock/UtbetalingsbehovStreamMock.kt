package no.nav.aap.app.stream.mock

import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoLøsning
import no.nav.aap.kafka.streams.consume
import no.nav.aap.kafka.streams.filter
import no.nav.aap.kafka.streams.filterNotNull
import no.nav.aap.kafka.streams.produce
import org.apache.kafka.streams.StreamsBuilder

internal fun StreamsBuilder.utbetalingsbehovStreamMock() {
    consume(Topics.utbetalingsbehov)
        .filterNotNull("mock-filter-utbetalingsbehov-tombstone")
        .filter("mock-filter-utbetalingsbehov-request") { _, løsning -> løsning.response == null }
        .filter("mock-utbetalingsbehov-filter-for-test-ident") { _, løsning -> løsning.request.ident != "123" }
        .mapValues { _, utbetalingsbehov ->
            utbetalingsbehov.copy(
                response = KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Response(
                    DtoLøsning(emptyList())
                )
            )
        }
        .produce(Topics.utbetalingsbehov, "mock-produced-utbetalingsbehov-response")
}
