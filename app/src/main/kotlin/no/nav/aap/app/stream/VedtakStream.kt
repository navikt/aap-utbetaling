package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.app.kafka.toDto
import no.nav.aap.app.kafka.toJson
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.dto.DtoVedtakshendelse
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
    val dtoMottaker = mottakerKafkaDto?.toDto() ?: DtoMottaker.opprettMottaker(ident, kafkaDto.fødselsdato)
    val dtoVedtakshendelse = gjenopprett(kafkaDto)
    val endretDtoMottaker = dtoVedtakshendelse.håndter(dtoMottaker)
    endretDtoMottaker.toJson(mottakerKafkaDto?.sekvensnummer ?: MottakereKafkaDto.INIT_SEKVENS)
}

private fun gjenopprett(kafkaDto: IverksettVedtakKafkaDto) = DtoVedtakshendelse(
    vedtaksid = kafkaDto.vedtaksid,
    innvilget = kafkaDto.innvilget,
    grunnlagsfaktor = kafkaDto.grunnlagsfaktor,
    vedtaksdato = kafkaDto.vedtaksdato,
    virkningsdato = kafkaDto.virkningsdato,
    fødselsdato = kafkaDto.fødselsdato,
)
