package no.nav.aap.app.kafka

import no.nav.aap.kafka.serde.json.JsonSerde
import no.nav.aap.kafka.streams.Topic
import java.time.LocalDate
import java.util.*

object Topics {
    val vedtak = Topic("aap.aap-vedtak-fattet.v1", JsonSerde.jackson<KafkaVedtak>())
    val barn = Topic("aap.barn.v1", JsonSerde.jackson<KafkaBarna>())
    val institusjon = Topic("aap.barn.v1", JsonSerde.jackson<KafkaInstitusjon>())
    val meldeplikt = Topic("aap.meldeplikt.v1", JsonSerde.jackson<KafkaMeldeplikt>())

}

data class KafkaVedtak(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)

data class KafkaBarna(
    val barn: List<KafkaBarn>
)

data class KafkaBarn(
    val fødselsnummer: String,
    val fødselsdato: LocalDate,
    // Hvor mye inntekt?
)

// 11-25
data class KafkaInstitusjon(
    val institusjonsnavn: String,
    val periodeFom: LocalDate,
    val periodeTom: LocalDate
    // Forsørgeransvar her?
)

data class KafkaMeldeplikt(
    val aktivitetPerDag: List<KafkaBrukersAktivitetPerDag>
)

data class KafkaBrukersAktivitetPerDag(
    val dato: LocalDate
)