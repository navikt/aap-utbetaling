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
    barnetillegg = emptyList(), // barnetillegg.map(Barna::toDto),
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

internal fun UtbetalingstidslinjedagKafkaDto.toKafkaDto() = MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto(
    type = if (grunnlagsfaktor != null) "UTBETALINGSDAG" else "IKKE_UTBETALINGSDAG",
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse?.let { MottakereKafkaDto.Paragraf_11_20_1_ledd_KafkaDto(0.66, grunnlag!!, it) },
    dagsats = dagsats?.let { MottakereKafkaDto.Paragraf_11_20_2_ledd_2_punktum_KafkaDto(260,årligYtelse!!, it)},
    høyesteÅrligYtelseMedBarnetillegg = grunnlag?.times(.9),
    høyesteBeløpMedBarnetillegg = høyestebeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = barnetillegg?.let { dagsats?.plus(it) },
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
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
