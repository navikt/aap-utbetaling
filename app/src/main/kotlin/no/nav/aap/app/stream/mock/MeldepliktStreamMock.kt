package no.nav.aap.app.stream.mock

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.kafka.streams.filter
import no.nav.aap.kafka.streams.filterNotNull
import no.nav.aap.kafka.streams.produce
import org.apache.kafka.streams.StreamsBuilder

internal fun StreamsBuilder.meldepliktStreamMock() {
    mockConsume(Topics.vedtak)
        .filterNotNull("mock-filter-meldeplikt-tombstone")
        .filter("mock-meldeplikt-filter-for-test-ident") { ident, _ -> ident != "123" }
        .mapValues { _, vedtak ->
            DtoMeldepliktshendelse(
                (0.until(14L))
                    .map { vedtak.virkningsdato.plusDays(it) }
                    .map { DtoAkivitetPerDag(it, 0.0, false) }
            )
        }
        .produce(Topics.meldeplikt, "mock-produced-meldeplikt-response")
}
