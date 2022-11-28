package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto
import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto.*
import no.nav.aap.dto.kafka.MottakereKafkaDto
import java.time.LocalDate

internal fun ForrigeMottakereKafkaDto.toKafkaDto() = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(VedtakKafkaDto::toKafkaDto),
    aktivitetstidslinje = aktivitetstidslinje.map(MeldeperiodeKafkaDto::toKafkaDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map { it.toKafkaDto(fødselsdato) },
    oppdragshistorikk = oppdragshistorikk.map(OppdragKafkaDto::toKafkaDto),
    barnetillegg = barnetillegg.map(BarnaKafkaDto::toKafkaDto),
    tilstand = tilstand,
)

internal fun VedtakKafkaDto.toKafkaDto() = MottakereKafkaDto.VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun MeldeperiodeKafkaDto.toKafkaDto() = MottakereKafkaDto.MeldeperiodeKafkaDto(
    dager = dager.map(DagKafkaDto::toKafkaDto)
)

internal fun DagKafkaDto.toKafkaDto() = MottakereKafkaDto.DagKafkaDto(
    dato = dato,
    arbeidstimer = arbeidstimer,
    type = type
)

internal fun UtbetalingstidslinjeKafkaDto.toKafkaDto(fødselsdato: LocalDate) = MottakereKafkaDto.UtbetalingstidslinjeKafkaDto(
    dager = dager.map { it.toKafkaDto(fødselsdato) }
)

internal fun UtbetalingstidslinjedagKafkaDto.toKafkaDto(fødselsdato: LocalDate) =
    MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = utbetalingsdag?.toKafkaDto(fødselsdato),
        ikkeUtbetalingsdag = ikkeUtbetalingsdag?.toKafkaDto(),
    )

//FIXME: Fjern fødselsdato-parameter
internal fun UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto.toKafkaDto(fødselsdato: LocalDate) =
    MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto(
        dato = dato,
        //FIXME: Hent fødselsdato fra utbetalingsdag, og ikke fra parameter
        fødselsdato = fødselsdato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnlagsfaktorJustertForAlder = grunnlagsfaktor,
        barnetillegg = barnetillegg,
        grunnlag = grunnlag.toKafkaDto(),
        årligYtelse = årligYtelse.toKafkaDto(),
        dagsats = dagsats.toKafkaDto(),
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toKafkaDto(),
        høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
        dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
        beløpMedBarnetillegg = beløpMedBarnetillegg,
        beløp = beløp,
        arbeidsprosent = arbeidsprosent,
    )

internal fun UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto.toKafkaDto() =
    MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto(
        dato = dato,
        arbeidsprosent = arbeidsprosent,
    )

internal fun Paragraf_11_19_3_leddKafkaDto.toKafkaDto() =
    MottakereKafkaDto.Paragraf_11_19_3_leddKafkaDto(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnlag = grunnlag
    )

internal fun Paragraf_11_20_2_ledd_2_punktum_KafkaDto.toKafkaDto() =
    MottakereKafkaDto.Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
        antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
        årligYtelse = årligYtelse,
        dagsats = dagsats
    )

internal fun Paragraf_11_20_1_ledd_KafkaDto.toKafkaDto() =
    MottakereKafkaDto.Paragraf_11_20_1_ledd_KafkaDto(
        faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
        inntektsgrunnlag = inntektsgrunnlag,
        årligytelse = årligytelse
    )

internal fun Paragraf_11_20_6_leddKafkaDto.toKafkaDto() =
    MottakereKafkaDto.Paragraf_11_20_6_leddKafkaDto(
        maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
        grunnlag = grunnlag,
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
    )

internal fun OppdragKafkaDto.toKafkaDto() = MottakereKafkaDto.OppdragKafkaDto(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(UtbetalingslinjeKafkaDto::toKafkaDto),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

internal fun UtbetalingslinjeKafkaDto.toKafkaDto() = MottakereKafkaDto.UtbetalingslinjeKafkaDto(
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

internal fun BarnaKafkaDto.toKafkaDto() = MottakereKafkaDto.BarnaKafkaDto(
    barn = barn.map(BarnKafkaDto::toKafkaDto)
)

internal fun BarnKafkaDto.toKafkaDto() = MottakereKafkaDto.BarnKafkaDto(
    fødselsdato = fødselsdato
)
