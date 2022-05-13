package no.nav.aap.app.kafka

import no.nav.aap.app.stream.BehovUtbetaling
import no.nav.aap.app.stream.BehovUtbetalingVisitor
import no.nav.aap.domene.utbetaling.dto.DtoLøsning

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
        data class Request(
            val ident: String
        )

        data class Response(
            val barn: DtoLøsning
        )
    }
}
