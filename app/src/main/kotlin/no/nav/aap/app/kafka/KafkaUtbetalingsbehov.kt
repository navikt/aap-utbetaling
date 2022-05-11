package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.dto.DtoLøsning

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
