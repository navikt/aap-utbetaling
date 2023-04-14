package app

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.aap.domene.utbetaling.modellapi.LøsningBarnModellApi
import no.nav.aap.domene.utbetaling.modellapi.LøsningModellApi
import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApi
import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApiObserver
import no.nav.aap.kafka.streams.v2.Streams
import app.simulering.SimuleringRequest
import app.simulering.SimuleringResponse
import java.time.LocalDate

internal fun Routing.simulering() {
    route("/simuler") {
        post("/{personident}") {
            val personident = requireNotNull(call.parameters["personident"]) { "Personident må være satt" }
            val simuleringRequest = call.receive<SimuleringRequest>()

            val mottaker = MottakerModellApi.opprettMottaker(personident, simuleringRequest.fødselsdato)
            val vedtakshendelse = simuleringRequest.lagVedtakshendelse()
            val mottakerMedVedtak = vedtakshendelse.håndter(mottaker)
            val meldepliktshendelse = simuleringRequest.lagMeldepliktshendelse()
            val endretMottaker = meldepliktshendelse.håndter(mottakerMedVedtak, object : MottakerModellApiObserver {})
            val endretMottakerMedBarn = LøsningModellApi(listOf(LøsningBarnModellApi(LocalDate.of(2018, 11, 1)))).håndter(endretMottaker)

            call.respond(SimuleringResponse.lagNy(endretMottakerMedBarn))
        }
    }
}

internal fun Routing.actuator(prometheus: PrometheusMeterRegistry, kafka: Streams) {
    route("/actuator") {
        get("/metrics") {
            call.respondText { prometheus.scrape() }
        }
        get("/live") {
            val status = if (kafka.live()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("utbetaling", status = status)
        }
        get("/ready") {
            val status = if (kafka.ready()) HttpStatusCode.OK else HttpStatusCode.InternalServerError
            call.respondText("utbetaling", status = status)
        }
    }
}
