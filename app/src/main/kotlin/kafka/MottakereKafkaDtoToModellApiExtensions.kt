package kafka

import no.nav.aap.domene.utbetaling.modellapi.*
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto.*

internal fun MottakereKafkaDto.toModellApi() = MottakerModellApi(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(VedtakKafkaDto::toModellApi),
    aktivitetstidslinje = aktivitetstidslinje.map(MeldeperiodeKafkaDto::toModellApi),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(UtbetalingstidslinjeKafkaDto::toModellApi),
    oppdragshistorikk = oppdragshistorikk.map(OppdragKafkaDto::toModellApi),
    barnetillegg = barnetillegg.map(BarnaKafkaDto::toModellApi),
    tilstand = tilstand,
)

private fun VedtakKafkaDto.toModellApi() = VedtakModellApi(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

private fun MeldeperiodeKafkaDto.toModellApi() = MeldeperiodeModellApi(
    dager = dager.mapNotNull(DagKafkaDto::toModellApi)
)

private fun DagKafkaDto.toModellApi() =
    helgedag?.toModellApi() ?: arbeidsdag?.toModellApi() ?: fraværsdag?.toModellApi()

private fun DagKafkaDto.HelgedagKafkaDto.toModellApi() = DagModellApi.HelgedagModellApi(
    dato = dato,
    arbeidstimer = arbeidstimer,
)

private fun DagKafkaDto.ArbeidsdagKafkaDto.toModellApi() = DagModellApi.ArbeidsdagModellApi(
    dato = dato,
    arbeidstimer = arbeidstimer,
)

private fun DagKafkaDto.FraværsdagKafkaDto.toModellApi() = DagModellApi.FraværsdagModellApi(
    dato = dato,
)

private fun UtbetalingstidslinjeKafkaDto.toModellApi() = UtbetalingstidslinjeModellApi(
    dager = dager.mapNotNull(UtbetalingstidslinjedagKafkaDto::toModellApi)
)

private fun UtbetalingstidslinjedagKafkaDto.toModellApi() =
    utbetalingsdag?.toModellApi() ?: ikkeUtbetalingsdag?.toModellApi()

private fun UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto.toModellApi() =
    UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi(
        dato = dato,
        fødselsdato = fødselsdato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnlagsfaktorJustertForAlder = grunnlagsfaktorJustertForAlder,
        barnetillegg = barnetillegg,
        grunnlag = grunnlag.toModellApi(),
        årligYtelse = årligYtelse.toModellApi(),
        dagsats = dagsats.toModellApi(),
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toModellApi(),
        høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg.toModellApi(),
        dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.toModellApi(),
        beløpMedBarnetillegg = beløpMedBarnetillegg,
        beløp = beløp,
        arbeidsprosent = arbeidsprosent,
    )

private fun UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto.toModellApi() =
    UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi(
        dato = dato,
        arbeidsprosent = arbeidsprosent,
    )

private fun Paragraf_11_19_3_leddKafkaDto.toModellApi() = Paragraf_11_19_3_leddModellApi(
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    grunnbeløp = grunnbeløp,
    grunnlag = grunnlag
)

private fun Paragraf_11_20_2_ledd_2_punktum_KafkaDto.toModellApi() = Paragraf_11_20_2_ledd_2_punktum_ModellApi(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

private fun Paragraf_11_20_1_ledd_KafkaDto.toModellApi() = Paragraf_11_20_1_ledd_ModellApi(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    grunnlag = grunnlag,
    årligytelse = årligytelse
)

private fun Paragraf_11_20_3_5_ledd_KafkaDto.toModellApi() = Paragraf_11_20_3_5_ledd_ModellApi(
    dagsats = dagsats,
    barnetillegg = barnetillegg,
    beløp = beløp
)

private fun Paragraf_11_20_6_leddKafkaDto.toModellApi() = Paragraf_11_20_6_leddModellApi(
    maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
    grunnlag = grunnlag,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
)

private fun OppdragKafkaDto.toModellApi() = OppdragModellApi(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(UtbetalingslinjeKafkaDto::toModellApi),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

private fun UtbetalingslinjeKafkaDto.toModellApi() = UtbetalingslinjeModellApi(
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

private fun BarnaKafkaDto.toModellApi() = BarnaModellApi(
    barn = barn.map(BarnKafkaDto::toModellApi)
)

private fun BarnKafkaDto.toModellApi() = BarnModellApi(
    fødselsdato = fødselsdato
)
