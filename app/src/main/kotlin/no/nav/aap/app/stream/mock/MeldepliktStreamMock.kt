package no.nav.aap.app.stream.mock

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.dto.DtoAkivitetPerDag
import no.nav.aap.domene.utbetaling.dto.DtoMeldepliktshendelse
import no.nav.aap.kafka.streams.extension.filterKeys
import no.nav.aap.kafka.streams.extension.filterNotNull
import no.nav.aap.kafka.streams.extension.mapValues
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder

internal fun StreamsBuilder.meldepliktStreamMock() {
    mockConsume(Topics.vedtak)
        .filterNotNull("mock-meldeplikt-filter-tombstone")
        .filterKeys("mock-meldeplikt-filter-for-test-ident") { ident -> ident != "123" }
        .mapValues("mock-meldeplikt-opprett-meldepliktshendelse") { vedtak ->
            DtoMeldepliktshendelse(
                (0.until(14L))
                    .map { vedtak.virkningsdato.plusDays(it) }
                    .map { DtoAkivitetPerDag(it, 0.0, false) }
            )
        }
        .produce(Topics.meldeplikt, "mock-meldeplikt-produced-response")
}
