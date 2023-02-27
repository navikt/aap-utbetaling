package app.kafka

import app.stream.BehovUtbetaling
import app.stream.BehovUtbetalingVisitor
import no.nav.aap.domene.utbetaling.modellapi.LøsningModellApi

internal data class KafkaUtbetalingsbehovWrapper(
    private val behov: KafkaUtbetalingsbehov
) : BehovUtbetaling {

    override fun erUtbetalingsbehov() = true
    override fun accept(visitor: BehovUtbetalingVisitor) {
        visitor.utbetalingsbehov(behov)
    }

    data class KafkaUtbetalingsbehov(
        val request: Request,
        val response: Response?
    ) {
        class Request

        data class Response(
            val barn: LøsningModellApi
        )
    }
}
