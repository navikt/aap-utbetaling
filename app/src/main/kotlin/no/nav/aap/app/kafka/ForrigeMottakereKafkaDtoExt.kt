package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto
import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto.*
import no.nav.aap.dto.kafka.MottakereKafkaDto

internal fun ForrigeMottakereKafkaDto.toKafkaDto() = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(VedtakKafkaDto::toKafkaDto),
    aktivitetstidslinje = aktivitetstidslinje.map(MeldeperiodeKafkaDto::toKafkaDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(UtbetalingstidslinjeKafkaDto::toKafkaDto),
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

internal fun UtbetalingstidslinjeKafkaDto.toKafkaDto() = MottakereKafkaDto.UtbetalingstidslinjeKafkaDto(
    dager = dager.map(UtbetalingstidslinjedagKafkaDto::toKafkaDto)
)

internal fun UtbetalingstidslinjedagKafkaDto.toKafkaDto() =
    MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = utbetalingsdag?.toKafkaDto(),
        ikkeUtbetalingsdag = ikkeUtbetalingsdag?.toKafkaDto(),
    )

internal fun UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto.toKafkaDto() =
    MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto(
        dato = dato,
        fødselsdato = fødselsdato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnlagsfaktorJustertForAlder = grunnlagsfaktorJustertForAlder,
        barnetillegg = barnetillegg,
        grunnlag = grunnlag.toKafkaDto(),
        årligYtelse = årligYtelse.toKafkaDto(),
        dagsats = dagsats.toKafkaDto(),
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toKafkaDto(),
        //FIXME: Bytt ut med høyesteBeløpMedBarnetillegg.toKafkaDto() etter migrering
        høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg.let {
            MottakereKafkaDto.Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
                antallDagerMedUtbetalingPerÅr = 260,
                årligYtelse = høyesteÅrligYtelseMedBarnetillegg.høyesteÅrligYtelseMedBarnetillegg,
                dagsats = it,
            )
        },
        //FIXME: Bytt ut med dagsatsMedBarnetillegg.toKafkaDto() etter migrering
        dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.let {
            MottakereKafkaDto.Paragraf_11_20_3_5_ledd_KafkaDto(
                dagsats = dagsats.dagsats,
                barnetillegg = barnetillegg,
                beløp = it,
            )
        },
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
        grunnbeløp = grunnbeløp,
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
        grunnlag = grunnlag,
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
