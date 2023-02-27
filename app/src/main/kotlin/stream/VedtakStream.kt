package stream

import kafka.Topics
import kafka.buffer
import kafka.toModellApi
import kafka.toMottakereKafkaDtoHistorikk
import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApi
import no.nav.aap.domene.utbetaling.modellapi.VedtakshendelseModellApi
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDtoHistorikk
import no.nav.aap.kafka.streams.v2.KTable
import no.nav.aap.kafka.streams.v2.KeyValue
import no.nav.aap.kafka.streams.v2.Topology

internal fun Topology.vedtakStream(mottakerKtable: KTable<MottakereKafkaDtoHistorikk>) {
    consume(Topics.vedtak)
        .leftJoinWith(mottakerKtable, mottakerKtable.buffer)
        .mapKeyValue(::håndter)
        .produce(Topics.mottakere, mottakerKtable.buffer) { it }
}

private fun håndter(
    personident: String,
    iverksettVedtakKafkaDto: IverksettVedtakKafkaDto,
    mottakereKafkaDtoHistorikk: MottakereKafkaDtoHistorikk?,
): KeyValue<String, MottakereKafkaDtoHistorikk> {
    val mottakerModellApi = mottakereKafkaDtoHistorikk?.mottakereKafkaDto?.toModellApi()
        ?: MottakerModellApi.opprettMottaker(personident, iverksettVedtakKafkaDto.fødselsdato)
    val vedtakshendelseModellApi = gjenopprett(iverksettVedtakKafkaDto)
    val endretDtoMottaker = vedtakshendelseModellApi.håndter(mottakerModellApi)
    return KeyValue(
        personident,
        endretDtoMottaker.toMottakereKafkaDtoHistorikk(mottakereKafkaDtoHistorikk?.sekvensnummer ?: 0)
    )
}

private fun gjenopprett(kafkaDto: IverksettVedtakKafkaDto) = VedtakshendelseModellApi(
    vedtaksid = kafkaDto.vedtaksid,
    innvilget = kafkaDto.innvilget,
    grunnlagsfaktor = kafkaDto.grunnlagsfaktor,
    vedtaksdato = kafkaDto.vedtaksdato,
    virkningsdato = kafkaDto.virkningsdato,
    fødselsdato = kafkaDto.fødselsdato,
)
