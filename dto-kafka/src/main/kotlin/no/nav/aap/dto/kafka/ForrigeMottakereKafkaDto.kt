package no.nav.aap.dto.kafka

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class ForrigeMottakereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<Vedtak>,
    val aktivitetstidslinje: List<Meldeperiode>,
    val utbetalingstidslinjehistorikk: List<Utbetalingstidslinje>,
    val oppdragshistorikk: List<Oppdrag>,
    val barnetillegg: List<Barna>,
    val tilstand: String,
) {

    data class Vedtak(
        val vedtaksid: UUID,
        val innvilget: Boolean,
        val grunnlagsfaktor: Double,
        val vedtaksdato: LocalDate,
        val virkningsdato: LocalDate,
        val fødselsdato: LocalDate
    )

    data class Meldeperiode(
        val dager: List<Dag>
    )

    data class Dag(
        val dato: LocalDate,
        val arbeidstimer: Double?,
        val type: String
    )

    data class Utbetalingstidslinje(
        val dager: List<Utbetalingstidslinjedag>
    )

    data class Utbetalingstidslinjedag(
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

    data class Oppdrag(
        val mottaker: String,
        val fagområde: String,
        val linjer: List<Utbetalingslinje>,
        val fagsystemId: String,
        val endringskode: String,
        val nettoBeløp: Int,
        val overføringstidspunkt: LocalDateTime?,
        val avstemmingsnøkkel: Long?,
        val status: String?,
        val tidsstempel: LocalDateTime
    )

    data class Utbetalingslinje(
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

    data class Barna(
        val barn: List<Barn>
    )

    data class Barn(
        val fødselsdato: LocalDate
    )
}
