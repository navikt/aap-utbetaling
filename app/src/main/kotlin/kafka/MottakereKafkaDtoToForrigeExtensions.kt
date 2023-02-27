package kafka

import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto as Til
import no.nav.aap.dto.kafka.MottakereKafkaDto as Fra

internal fun Fra.toForrigeKafkaDto() = Til(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(Fra.VedtakKafkaDto::toKafkaDto),
    aktivitetstidslinje = aktivitetstidslinje.map(Fra.MeldeperiodeKafkaDto::toKafkaDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(Fra.UtbetalingstidslinjeKafkaDto::toKafkaDto),
    oppdragshistorikk = oppdragshistorikk.map(Fra.OppdragKafkaDto::toKafkaDto),
    barnetillegg = barnetillegg.map(Fra.BarnaKafkaDto::toKafkaDto),
    tilstand = tilstand,
)

private fun Fra.VedtakKafkaDto.toKafkaDto() = Til.VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

private fun Fra.MeldeperiodeKafkaDto.toKafkaDto() = Til.MeldeperiodeKafkaDto(
    dager = dager.map(Fra.DagKafkaDto::toKafkaDto)
)

private fun Fra.DagKafkaDto.toKafkaDto() = Til.DagKafkaDto(
    helgedag = helgedag?.toKafkaDto(),
    arbeidsdag = arbeidsdag?.toKafkaDto(),
    fraværsdag = fraværsdag?.toKafkaDto(),
)

private fun Fra.DagKafkaDto.HelgedagKafkaDto.toKafkaDto() = Til.DagKafkaDto.HelgedagKafkaDto(
    dato = dato,
    arbeidstimer = arbeidstimer,
)

private fun Fra.DagKafkaDto.ArbeidsdagKafkaDto.toKafkaDto() = Til.DagKafkaDto.ArbeidsdagKafkaDto(
    dato = dato,
    arbeidstimer = arbeidstimer,
)

private fun Fra.DagKafkaDto.FraværsdagKafkaDto.toKafkaDto() = Til.DagKafkaDto.FraværsdagKafkaDto(
    dato = dato,
)

private fun Fra.UtbetalingstidslinjeKafkaDto.toKafkaDto() = Til.UtbetalingstidslinjeKafkaDto(
    dager = dager.map(Fra.UtbetalingstidslinjedagKafkaDto::toKafkaDto)
)

private fun Fra.UtbetalingstidslinjedagKafkaDto.toKafkaDto() =
    Til.UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = utbetalingsdag?.toKafkaDto(),
        ikkeUtbetalingsdag = ikkeUtbetalingsdag?.toKafkaDto(),
    )

private fun Fra.UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto.toKafkaDto() =
    Til.UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto(
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
    )

private fun Fra.UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto.toKafkaDto() =
    Til.UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto(
        dato = dato,
        arbeidsprosent = arbeidsprosent,
    )

private fun Fra.Paragraf_11_19_3_leddKafkaDto.toKafkaDto() =
    Til.Paragraf_11_19_3_leddKafkaDto(
        dato = dato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnbeløp = grunnbeløp,
        grunnlag = grunnlag
    )

private fun Fra.Paragraf_11_20_2_ledd_2_punktum_KafkaDto.toKafkaDto() =
    Til.Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
        antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
        årligYtelse = årligYtelse,
        dagsats = dagsats
    )

private fun Fra.Paragraf_11_20_1_ledd_KafkaDto.toKafkaDto() =
    Til.Paragraf_11_20_1_ledd_KafkaDto(
        faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
        grunnlag = grunnlag,
        årligytelse = årligytelse
    )

private fun Fra.Paragraf_11_20_3_5_ledd_KafkaDto.toKafkaDto() = Til.Paragraf_11_20_3_5_ledd_KafkaDto(
    dagsats = dagsats,
    barnetillegg = barnetillegg,
    beløp = beløp
)

private fun Fra.Paragraf_11_20_6_leddKafkaDto.toKafkaDto() =
    Til.Paragraf_11_20_6_leddKafkaDto(
        maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
        grunnlag = grunnlag,
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
    )

private fun Fra.OppdragKafkaDto.toKafkaDto() = Til.OppdragKafkaDto(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(Fra.UtbetalingslinjeKafkaDto::toKafkaDto),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

private fun Fra.UtbetalingslinjeKafkaDto.toKafkaDto() = Til.UtbetalingslinjeKafkaDto(
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

private fun Fra.BarnaKafkaDto.toKafkaDto() = Til.BarnaKafkaDto(
    barn = barn.map(Fra.BarnKafkaDto::toKafkaDto)
)

private fun Fra.BarnKafkaDto.toKafkaDto() = Til.BarnKafkaDto(
    fødselsdato = fødselsdato
)
