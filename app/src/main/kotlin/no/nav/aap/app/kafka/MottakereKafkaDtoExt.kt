package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.modellapi.*
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_1_ledd_ModellApi
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_2_ledd_2_punktum_ModellApi
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

internal fun VedtakKafkaDto.toModellApi() = VedtakModellApi(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun MeldeperiodeKafkaDto.toModellApi() = MeldeperiodeModellApi(
    dager = dager.map(DagKafkaDto::toModellApi)
)

internal fun DagKafkaDto.toModellApi() = DagModellApi(
    dato = dato, arbeidstimer = arbeidstimer, type = type
)

internal fun UtbetalingstidslinjeKafkaDto.toModellApi() = UtbetalingstidslinjeModellApi(
    dager = dager.map(UtbetalingstidslinjedagKafkaDto::toModellApi)
)

internal fun UtbetalingstidslinjedagKafkaDto.toModellApi() = UtbetalingstidslinjedagModellApi(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag?.toModellApi(),
    årligYtelse = årligYtelse?.toModellApi(),
    dagsats = dagsats?.toModellApi(),
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg?.toModellApi(),
    høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Paragraf_11_19_3_leddKafkaDto.toModellApi() = Paragraf_11_19_3_leddModellApi(
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    grunnlag = grunnlag
)

internal fun Paragraf_11_20_2_ledd_2_punktum_KafkaDto.toModellApi() = Paragraf_11_20_2_ledd_2_punktum_ModellApi(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

internal fun Paragraf_11_20_1_ledd_KafkaDto.toModellApi() = Paragraf_11_20_1_ledd_ModellApi(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    inntektsgrunnlag = inntektsgrunnlag,
    årligytelse = årligytelse
)

internal fun Paragraf_11_20_6_leddKafkaDto.toModellApi() = Paragraf_11_20_6_leddModellApi(
    maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
    grunnlag = grunnlag,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
)

internal fun OppdragKafkaDto.toModellApi() = OppdragModellApi(
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

internal fun UtbetalingslinjeKafkaDto.toModellApi() = UtbetalingslinjeModellApi(
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

internal fun BarnaKafkaDto.toModellApi() = BarnaModellApi(
    barn = barn.map(BarnKafkaDto::toModellApi)
)

internal fun BarnKafkaDto.toModellApi() = BarnModellApi(
    fødselsdato = fødselsdato
)

internal fun MottakerModellApi.toJson(gammelSekvensnummer: Long) = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(VedtakModellApi::toJson),
    aktivitetstidslinje = aktivitetstidslinje.map(MeldeperiodeModellApi::toJson),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(UtbetalingstidslinjeModellApi::toJson),
    oppdragshistorikk = oppdragshistorikk.map(OppdragModellApi::toJson),
    barnetillegg = barnetillegg.map(BarnaModellApi::toJson),
    tilstand = tilstand,
    sekvensnummer = gammelSekvensnummer + 1,
)

internal fun VedtakModellApi.toJson() = VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun MeldeperiodeModellApi.toJson() = MeldeperiodeKafkaDto(
    dager = dager.map(DagModellApi::toJson)
)

internal fun DagModellApi.toJson() = DagKafkaDto(
    dato = dato, arbeidstimer = arbeidstimer, type = type
)

internal fun UtbetalingstidslinjeModellApi.toJson() = UtbetalingstidslinjeKafkaDto(
    dager = dager.map(UtbetalingstidslinjedagModellApi::toJson)
)

internal fun UtbetalingstidslinjedagModellApi.toJson() = UtbetalingstidslinjedagKafkaDto(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag?.toKafkaDto(),
    årligYtelse = årligYtelse?.toKafkaDto(),
    dagsats = dagsats?.toKafkaDto(),
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg?.toKafkaDto(),
    høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Paragraf_11_19_3_leddModellApi.toKafkaDto() = Paragraf_11_19_3_leddKafkaDto(
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    grunnlag = grunnlag
)

internal fun Paragraf_11_20_2_ledd_2_punktum_ModellApi.toKafkaDto() = Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

internal fun Paragraf_11_20_1_ledd_ModellApi.toKafkaDto() = Paragraf_11_20_1_ledd_KafkaDto(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    inntektsgrunnlag = inntektsgrunnlag,
    årligytelse = årligytelse
)

internal fun Paragraf_11_20_6_leddModellApi.toKafkaDto() = Paragraf_11_20_6_leddKafkaDto(
    maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
    grunnlag = grunnlag,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
)

internal fun OppdragModellApi.toJson() = OppdragKafkaDto(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(UtbetalingslinjeModellApi::toJson),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

internal fun UtbetalingslinjeModellApi.toJson() = UtbetalingslinjeKafkaDto(
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

internal fun BarnaModellApi.toJson() = BarnaKafkaDto(
    barn = barn.map(BarnModellApi::toJson)
)

internal fun BarnModellApi.toJson() = BarnKafkaDto(
    fødselsdato = fødselsdato
)
