package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ForrigeMottakereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<VedtakKafkaDto>,
    val aktivitetstidslinje: List<MeldeperiodeKafkaDto>,
    val utbetalingstidslinjehistorikk: List<UtbetalingstidslinjeKafkaDto>,
    val oppdragshistorikk: List<OppdragKafkaDto>,
//    val barnetillegg: List<Barna>,
    val tilstand: String,
    val version: Int = MottakereKafkaDto.VERSION - 1,
) {

    data class VedtakKafkaDto(
        val vedtaksid: UUID,
        val innvilget: Boolean,
        val grunnlagsfaktor: Double,
        val vedtaksdato: LocalDate,
        val virkningsdato: LocalDate,
        val fødselsdato: LocalDate
    )

    data class MeldeperiodeKafkaDto(
        val dager: List<DagKafkaDto>
    )

    data class DagKafkaDto(
        val dato: LocalDate,
        val arbeidstimer: Double?,
        val type: String
    )

    data class UtbetalingstidslinjeKafkaDto(
        val dager: List<UtbetalingstidslinjedagKafkaDto>
    )

    data class UtbetalingstidslinjedagKafkaDto(
        val type: String,
        val dato: LocalDate,
        val grunnlagsfaktor: Double?,
        val barnetillegg: Double?,
        val grunnlag: Double?,
        val årligYtelse: Double?,
        val dagsats: Double?,
        val høyesteÅrligYtelseMedBarnetillegg: Double?,
        val høyestebeløpMedBarnetillegg: Double?,
        val dagsatsMedBarnetillegg: Double?,
        val beløpMedBarnetillegg: Double?,
        val beløp: Double?,
        val arbeidsprosent: Double
    )

    data class OppdragKafkaDto(
        val mottaker: String,
        val fagområde: String,
        val linjer: List<UtbetalingslinjeKafkaDto>,
        val fagsystemId: String,
        val endringskode: String,
        val nettoBeløp: Int,
        val overføringstidspunkt: LocalDateTime?,
        val avstemmingsnøkkel: Long?,
        val status: String?,
        val tidsstempel: LocalDateTime
    )

    data class UtbetalingslinjeKafkaDto(
        val fom: LocalDate,
        val tom: LocalDate,
        val satstype: String,
        val beløp: Int?,
        val aktuellDagsinntekt: Int?,
        val grad: Int?,
        val refFagsystemId: String?,
        val delytelseId: Int,
        val refDelytelseId: Int?,
        val endringskode: String,
        val klassekode: String,
        val datoStatusFom: LocalDate?
    )

    data class BarnaKafkaDto(
        val barn: List<BarnKafkaDto>
    )

    data class BarnKafkaDto(
        val fødselsdato: LocalDate
    )
}
