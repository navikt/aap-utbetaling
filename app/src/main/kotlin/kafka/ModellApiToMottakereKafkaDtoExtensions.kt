package kafka

import no.nav.aap.domene.utbetaling.modellapi.*
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto.*
import no.nav.aap.dto.kafka.MottakereKafkaDtoHistorikk

internal fun MottakerModellApi.toMottakereKafkaDtoHistorikk(gammelSekvensnummer: Long): MottakereKafkaDtoHistorikk {
    val mottakereKafkaDto = toKafkaDto()
    val forrigeMottakereKafkaDto = mottakereKafkaDto.toForrigeKafkaDto()
    return MottakereKafkaDtoHistorikk(
        mottakereKafkaDto = mottakereKafkaDto,
        forrigeMottakereKafkaDto = forrigeMottakereKafkaDto,
        sekvensnummer = gammelSekvensnummer + 1,
    )
}

private fun MottakerModellApi.toKafkaDto() = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(VedtakModellApi::toKafkaDto),
    aktivitetstidslinje = aktivitetstidslinje.map(MeldeperiodeModellApi::toKafkaDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(UtbetalingstidslinjeModellApi::toKafkaDto),
    oppdragshistorikk = oppdragshistorikk.map(OppdragModellApi::toKafkaDto),
    barnetillegg = barnetillegg.map(BarnaModellApi::toKafkaDto),
    tilstand = tilstand,
)

private fun VedtakModellApi.toKafkaDto() = VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

private fun MeldeperiodeModellApi.toKafkaDto() = MeldeperiodeKafkaDto(
    dager = dager.map(DagModellApi::toKafkaDto)
)

private fun DagModellApi.toKafkaDto() = object : DagModellApiVisitor {
    lateinit var dag: DagKafkaDto

    init {
        accept(this)
    }

    override fun visitHelgedag(helgedag: DagModellApi.HelgedagModellApi) {
        dag = helgedag.toKafkaDto()
    }

    override fun visitArbeidsdag(arbeidsdag: DagModellApi.ArbeidsdagModellApi) {
        dag = arbeidsdag.toKafkaDto()
    }

    override fun visitFraværsdag(fraværsdag: DagModellApi.FraværsdagModellApi) {
        dag = fraværsdag.toKafkaDto()
    }
}.dag

private fun DagModellApi.HelgedagModellApi.toKafkaDto() = DagKafkaDto(
    helgedag = DagKafkaDto.HelgedagKafkaDto(
        dato = dato,
        arbeidstimer = arbeidstimer,
    ),
    arbeidsdag = null,
    fraværsdag = null,
)

private fun DagModellApi.ArbeidsdagModellApi.toKafkaDto() = DagKafkaDto(
    helgedag = null,
    arbeidsdag = DagKafkaDto.ArbeidsdagKafkaDto(
        dato = dato,
        arbeidstimer = arbeidstimer,
    ),
    fraværsdag = null,
)

private fun DagModellApi.FraværsdagModellApi.toKafkaDto() = DagKafkaDto(
    helgedag = null,
    arbeidsdag = null,
    fraværsdag = DagKafkaDto.FraværsdagKafkaDto(
        dato = dato,
    ),
)

private fun UtbetalingstidslinjeModellApi.toKafkaDto() = UtbetalingstidslinjeKafkaDto(
    dager = dager.map(UtbetalingstidslinjedagModellApi::toKafkaDto)
)

private fun UtbetalingstidslinjedagModellApi.toKafkaDto() = object : UtbetalingstidslinjedagModellApiVisitor {
    lateinit var dag: UtbetalingstidslinjedagKafkaDto

    init {
        accept(this)
    }

    override fun visitUtbetalingsdag(utbetalingsdag: UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi) {
        dag = utbetalingsdag.toKafkaDto()
    }

    override fun visitIkkeUtbetalingsdag(ikkeUtbetalingsdag: UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi) {
        dag = ikkeUtbetalingsdag.toKafkaDto()
    }
}.dag

private fun UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi.toKafkaDto() =
    UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto(
            dato = dato,
            fødselsdato = fødselsdato,
            grunnlagsfaktor = grunnlagsfaktor,
            grunnlagsfaktorJustertForAlder = grunnlagsfaktorJustertForAlder,
            barnetillegg = barnetillegg,
            grunnlag = grunnlag.toKafkaDto(),
            årligYtelse = årligYtelse.toKafkaDto(),
            dagsats = dagsats.toKafkaDto(),
            høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toKafkaDto(),
            høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg.toKafkaDto(),
            dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.toKafkaDto(),
            beløpMedBarnetillegg = beløpMedBarnetillegg,
            beløp = beløp,
            arbeidsprosent = arbeidsprosent,
        ),
        ikkeUtbetalingsdag = null,
    )

private fun UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi.toKafkaDto() =
    UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = null,
        ikkeUtbetalingsdag = UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto(
            dato = dato,
            arbeidsprosent = arbeidsprosent,
        ),
    )

private fun Paragraf_11_19_3_leddModellApi.toKafkaDto() = Paragraf_11_19_3_leddKafkaDto(
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    grunnbeløp = grunnbeløp,
    grunnlag = grunnlag
)

private fun Paragraf_11_20_2_ledd_2_punktum_ModellApi.toKafkaDto() = Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

private fun Paragraf_11_20_1_ledd_ModellApi.toKafkaDto() = Paragraf_11_20_1_ledd_KafkaDto(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    grunnlag = grunnlag,
    årligytelse = årligytelse
)

private fun Paragraf_11_20_3_5_ledd_ModellApi.toKafkaDto() = Paragraf_11_20_3_5_ledd_KafkaDto(
    dagsats = dagsats,
    barnetillegg = barnetillegg,
    beløp = beløp
)

private fun Paragraf_11_20_6_leddModellApi.toKafkaDto() = Paragraf_11_20_6_leddKafkaDto(
    maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
    grunnlag = grunnlag,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
)

private fun OppdragModellApi.toKafkaDto() = OppdragKafkaDto(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(UtbetalingslinjeModellApi::toKafkaDto),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

private fun UtbetalingslinjeModellApi.toKafkaDto() = UtbetalingslinjeKafkaDto(
    fom = fom,
    tom = tom,
    satstype = satstype,
    beløp = beløp,
    aktuellDagsinntekt = aktuellDagsinntekt,
    grad = grad,
    refFagsystemId = refFagsystemId,
    delytelseId = delytelseId,
    refDelytelseId = refDelytelseId,
    endringskode = endringskode,
    klassekode = klassekode,
    datoStatusFom = datoStatusFom,
)

private fun BarnaModellApi.toKafkaDto() = BarnaKafkaDto(
    barn = barn.map(BarnModellApi::toKafkaDto)
)

private fun BarnModellApi.toKafkaDto() = BarnKafkaDto(
    fødselsdato = fødselsdato
)
