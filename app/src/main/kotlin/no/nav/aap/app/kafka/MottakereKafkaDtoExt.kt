package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.modellapi.*
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Paragraf_11_20_1_ledd_ModellAPI
import no.nav.aap.domene.utbetaling.utbetalingstidslinje.Paragraf_11_20_2_ledd_2_punktum_ModellAPI
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto.*

internal fun MottakereKafkaDto.toModellApi() = MottakerModellApi(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(Vedtak::toModellApi),
    aktivitetstidslinje = aktivitetstidslinje.map(Meldeperiode::toModellApi),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(Utbetalingstidslinje::toModellApi),
    oppdragshistorikk = oppdragshistorikk.map(Oppdrag::toModellApi),
    barnetillegg = barnetillegg.map(Barna::toModellApi),
    tilstand = tilstand,
)

internal fun Vedtak.toModellApi() = VedtakModellApi(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun Meldeperiode.toModellApi() = MeldeperiodeModellApi(
    dager = dager.map(Dag::toModellApi)
)

internal fun Dag.toModellApi() = DagModellApi(
    dato = dato, arbeidstimer = arbeidstimer, type = type
)

internal fun Utbetalingstidslinje.toModellApi() = UtbetalingstidslinjeModellApi(
    dager = dager.map(Utbetalingstidslinjedag::toModellApi)
)

internal fun Utbetalingstidslinjedag.toModellApi() = UtbetalingstidslinjedagModellApi(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse?.toModellApi(),
    dagsats = dagsats?.toModellApi(),
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg,
    høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Paragraf_11_20_2_ledd_2_punktum_KafkaDTO.toModellApi() = Paragraf_11_20_2_ledd_2_punktum_ModellAPI(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

internal fun Paragraf_11_20_1_ledd_KafkaDTO.toModellApi() = Paragraf_11_20_1_ledd_ModellAPI(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    inntektsgrunnlag = inntektsgrunnlag,
    årligytelse = årligytelse
)

internal fun Oppdrag.toModellApi() = OppdragModellApi(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(Utbetalingslinje::toModellApi),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

internal fun Utbetalingslinje.toModellApi() = UtbetalingslinjeModellApi(
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

internal fun Barna.toModellApi() = BarnaModellApi(
    barn = barn.map(Barn::toModellApi)
)

internal fun Barn.toModellApi() = BarnModellApi(
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

internal fun VedtakModellApi.toJson() = Vedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun MeldeperiodeModellApi.toJson() = Meldeperiode(
    dager = dager.map(DagModellApi::toJson)
)

internal fun DagModellApi.toJson() = Dag(
    dato = dato, arbeidstimer = arbeidstimer, type = type
)

internal fun UtbetalingstidslinjeModellApi.toJson() = Utbetalingstidslinje(
    dager = dager.map(UtbetalingstidslinjedagModellApi::toJson)
)

internal fun UtbetalingstidslinjedagModellApi.toJson() = Utbetalingstidslinjedag(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse?.toKafkaDTO(),
    dagsats = dagsats?.toKafkaDTO(),
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg,
    høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Paragraf_11_20_2_ledd_2_punktum_ModellAPI.toKafkaDTO() = Paragraf_11_20_2_ledd_2_punktum_KafkaDTO(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

internal fun Paragraf_11_20_1_ledd_ModellAPI.toKafkaDTO() = Paragraf_11_20_1_ledd_KafkaDTO(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    inntektsgrunnlag = inntektsgrunnlag,
    årligytelse = årligytelse
)

internal fun OppdragModellApi.toJson() = Oppdrag(
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

internal fun UtbetalingslinjeModellApi.toJson() = Utbetalingslinje(
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

internal fun BarnaModellApi.toJson() = Barna(
    barn = barn.map(BarnModellApi::toJson)
)

internal fun BarnModellApi.toJson() = Barn(
    fødselsdato = fødselsdato
)
