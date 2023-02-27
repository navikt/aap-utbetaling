package app.stream.mock

import app.kafka.KafkaUtbetalingsbehovWrapper
import app.kafka.Topics
import no.nav.aap.domene.utbetaling.modellapi.LøsningModellApi
import no.nav.aap.kafka.streams.v2.stream.ConsumedKStream


internal object UtbetalingsbehovMock {
    fun manglerResponse(behov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov) = behov.response == null

    fun mockResponseStream(stream: ConsumedKStream<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov>) {
        stream.filterKey { personident -> personident != "123" }
            .map { utbetalingsbehov ->
                utbetalingsbehov.copy(
                    response = KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Response(
                        LøsningModellApi(emptyList())
                    )
                )
            }
            .produce(Topics.utbetalingsbehov)
    }
}
