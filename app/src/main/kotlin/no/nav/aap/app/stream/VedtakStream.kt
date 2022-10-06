package no.nav.aap.app.stream

import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.dto.DtoMottaker
import no.nav.aap.domene.utbetaling.dto.DtoVedtakshendelse
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import no.nav.aap.dto.kafka.IverksettVedtakKafkaDto
import no.nav.aap.kafka.streams.extension.consume
import no.nav.aap.kafka.streams.extension.filterNotNull
import no.nav.aap.kafka.streams.extension.leftJoin
import no.nav.aap.kafka.streams.extension.produce
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable

internal fun StreamsBuilder.vedtakStream(mottakerKtable: KTable<String, DtoMottaker>) {
    consume(Topics.vedtak)
        .filterNotNull("vedtakshendelse-filter-tombstone")
        .leftJoin(Topics.vedtak with Topics.mottakere, mottakerKtable, håndter)
        .produce(Topics.mottakere, "produced-mottakere-for-vedtak")
}

private val håndter = { ident: String, kafkaDto: IverksettVedtakKafkaDto, dtoMottaker: DtoMottaker? ->
    val mottaker = dtoMottaker?.let { Mottaker.gjenopprett(it) } ?: Mottaker(
        Personident(ident),
        Fødselsdato(kafkaDto.fødselsdato)
    )
    val dtoVedtakshendelse = gjenopprett(kafkaDto)
    dtoVedtakshendelse.håndter(mottaker)
    mottaker.toDto()
}

private fun gjenopprett(kafkaDto: IverksettVedtakKafkaDto) = DtoVedtakshendelse(
    vedtaksid = kafkaDto.vedtaksid,
    innvilget = kafkaDto.innvilget,
    grunnlagsfaktor = kafkaDto.grunnlagsfaktor,
    vedtaksdato = kafkaDto.vedtaksdato,
    virkningsdato = kafkaDto.virkningsdato,
    fødselsdato = kafkaDto.fødselsdato,
)
