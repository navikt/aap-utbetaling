package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toModellApi
import no.nav.aap.app.kafka.toJson
import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApi
import no.nav.aap.domene.utbetaling.modellapi.VedtakshendelseModellApi
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.filterNotNull
import no.nav.aap.kafka.streams.extension.leftJoin
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.vedtakStream(mottakerKtable: KTable<String, MottakereKafkaDto>) {
    consume(Topics.vedtak)
        .filterNotNull("vedtakshendelse-filter-tombstone")
        .leftJoin(Topics.vedtak with Topics.mottakere, mottakerKtable, håndter)
        .produce(Topics.mottakere, "produced-mottakere-for-vedtak")
}

private val håndter = { ident: String, kafkaDto: IverksettVedtakKafkaDto, mottakerKafkaDto: MottakereKafkaDto? ->
    val mottakerModellApi = mottakerKafkaDto?.toModellApi() ?: MottakerModellApi.opprettMottaker(ident, kafkaDto.fødselsdato)
    val vedtakshendelseModellApi = gjenopprett(kafkaDto)
    val endretDtoMottaker = vedtakshendelseModellApi.håndter(mottakerModellApi)
    endretDtoMottaker.toJson(mottakerKafkaDto?.sekvensnummer ?: MottakereKafkaDto.INIT_SEKVENS)
}

private fun gjenopprett(kafkaDto: IverksettVedtakKafkaDto) = VedtakshendelseModellApi(
    vedtaksid = kafkaDto.vedtaksid,
    innvilget = kafkaDto.innvilget,
    grunnlagsfaktor = kafkaDto.grunnlagsfaktor,
    vedtaksdato = kafkaDto.vedtaksdato,
    virkningsdato = kafkaDto.virkningsdato,
    fødselsdato = kafkaDto.fødselsdato,
)
