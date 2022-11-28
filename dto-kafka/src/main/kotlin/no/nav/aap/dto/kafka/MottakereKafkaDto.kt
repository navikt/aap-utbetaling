package no.nav.aap.dto.kafka

import no.nav.aap.kafka.serde.json.Migratable
import no.nav.aap.kafka.streams.concurrency.Bufferable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class MottakereKafkaDto(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<VedtakKafkaDto>,
    val aktivitetstidslinje: List<MeldeperiodeKafkaDto>,
    val utbetalingstidslinjehistorikk: List<UtbetalingstidslinjeKafkaDto>,
    val oppdragshistorikk: List<OppdragKafkaDto>,
    val barnetillegg: List<BarnaKafkaDto>,
    val tilstand: String,
    val sekvensnummer: Long = INIT_SEKVENS,
    val version: Int = VERSION, // Denne bumpes ved hver migrering
) : Migratable, Bufferable<MottakereKafkaDto> {

    private var erMigrertAkkuratNå: Boolean = false

    companion object {
        const val VERSION = 5
        const val INIT_SEKVENS = 0L
    }

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
        val utbetalingsdag: UtbetalingsdagKafkaDto?,
        val ikkeUtbetalingsdag: IkkeUtbetalingsdagKafkaDto?,
    ) {
        data class UtbetalingsdagKafkaDto(
            val dato: LocalDate,
            val fødselsdato: LocalDate,
            val grunnlagsfaktor: Double,
            val grunnlagsfaktorJustertForAlder: Double,
            val barnetillegg: Double,
            val grunnlag: Paragraf_11_19_3_leddKafkaDto,
            val årligYtelse: Paragraf_11_20_1_ledd_KafkaDto,
            val dagsats: Paragraf_11_20_2_ledd_2_punktum_KafkaDto,
            val høyesteÅrligYtelseMedBarnetillegg: Paragraf_11_20_6_leddKafkaDto,
            val høyesteBeløpMedBarnetillegg: Double,
            val dagsatsMedBarnetillegg: Double,
            val beløpMedBarnetillegg: Double,
            val beløp: Double,
            val arbeidsprosent: Double
        )

        data class IkkeUtbetalingsdagKafkaDto(
            val dato: LocalDate,
            val arbeidsprosent: Double
        )
    }

    data class Paragraf_11_19_3_leddKafkaDto(
        val dato: LocalDate,
        val grunnlagsfaktor: Double,
        val grunnlag: Double
    )

    data class Paragraf_11_20_1_ledd_KafkaDto(
        val faktorForReduksjonAvGrunnlag: Double,
        val inntektsgrunnlag: Double,
        val årligytelse: Double
    )

    data class Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
        val antallDagerMedUtbetalingPerÅr: Int,
        val årligYtelse: Double,
        val dagsats: Double
    )

    data class Paragraf_11_20_6_leddKafkaDto(
        val maksFaktorAvGrunnlag: Double,
        val grunnlag: Double,
        val høyesteÅrligYtelseMedBarnetillegg: Double
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

    override fun markerSomMigrertAkkuratNå() {
        erMigrertAkkuratNå = true
    }

    override fun erMigrertAkkuratNå(): Boolean = erMigrertAkkuratNå

    override fun erNyere(other: MottakereKafkaDto): Boolean = sekvensnummer > other.sekvensnummer
}
