package no.nav.aap.app.kafka

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Joined
import org.apache.kafka.streams.kstream.Produced
import java.time.LocalDate
import java.util.*

data class Topic<K, V>(
    val name: String,
    val keySerde: Serde<K>,
    val valueSerde: Serde<V>,
) {
    fun consumed(named: String): Consumed<K, V> = Consumed.with(keySerde, valueSerde).withName(named)
    fun produced(named: String): Produced<K, V> = Produced.with(keySerde, valueSerde).withName(named)
    fun <R : Any> joined(right: Topic<K, R>): Joined<K, V, R> =
        Joined.with(keySerde, valueSerde, right.valueSerde, "$name-joined-${right.name}")
}

class Topics(private val config: KafkaConfig) {
    val vedtak = Topic("aap.aap-vedtak-fattet.v1", Serdes.StringSerde(), jsonSerde<KafkaVedtak>())
    val barn = Topic("aap.barn.v1", Serdes.StringSerde(), jsonSerde<KafkaBarna>())
    val institusjon = Topic("aap.barn.v1", Serdes.StringSerde(), jsonSerde<KafkaInstitusjon>())
    val meldeplikt = Topic("aap.meldeplikt.v1", Serdes.StringSerde(), jsonSerde<KafkaMeldeplikt>())

    private inline fun <reified V : Any> jsonSerde(): Serde<V> = JsonSerde(V::class)

    private fun <T : SpecificRecord> avroSerde(): SpecificAvroSerde<T> = SpecificAvroSerde<T>().apply {
        val avroProperties = config.schemaRegistry + config.ssl
        val avroConfig = avroProperties.map { it.key.toString() to it.value.toString() }
        configure(avroConfig.toMap(), false)
    }
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