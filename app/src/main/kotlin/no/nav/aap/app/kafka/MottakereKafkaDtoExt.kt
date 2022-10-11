package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.dto.*
import no.nav.aap.dto.kafka.MottakereKafkaDto
import no.nav.aap.dto.kafka.MottakereKafkaDto.*

internal fun MottakereKafkaDto.toDto() = DtoMottaker(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(Vedtak::toDto),
    aktivitetstidslinje = aktivitetstidslinje.map(Meldeperiode::toDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(Utbetalingstidslinje::toDto),
    oppdragshistorikk = oppdragshistorikk.map(Oppdrag::toDto),
    barnetillegg = barnetillegg.map(Barna::toDto),
    tilstand = tilstand,
)

internal fun Vedtak.toDto() = DtoVedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun Meldeperiode.toDto() = DtoMeldeperiode(
    dager = dager.map(Dag::toDto)
)

internal fun Dag.toDto() = DtoDag(
    dato = dato,
    arbeidstimer = arbeidstimer,
    type = type
)

internal fun Utbetalingstidslinje.toDto() = DtoUtbetalingstidslinje(
    dager = dager.map(Utbetalingstidslinjedag::toDto)
)

internal fun Utbetalingstidslinjedag.toDto() = DtoUtbetalingstidslinjedag(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse,
    dagsats = dagsats,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg,
    høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Oppdrag.toDto() = DtoOppdrag(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(Utbetalingslinje::toDto),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

internal fun Utbetalingslinje.toDto() = DtoUtbetalingslinje(
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

internal fun Barna.toDto() = DtoBarna(
    barn = barn.map(Barn::toDto)
)

internal fun Barn.toDto() = DtoBarn(
    fødselsdato = fødselsdato
)

internal fun DtoMottaker.toJson(gammelSekvensnummer: Long) = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(DtoVedtak::toJson),
    aktivitetstidslinje = aktivitetstidslinje.map(DtoMeldeperiode::toJson),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(DtoUtbetalingstidslinje::toJson),
    oppdragshistorikk = oppdragshistorikk.map(DtoOppdrag::toJson),
    barnetillegg = barnetillegg.map(DtoBarna::toJson),
    tilstand = tilstand,
    sekvensnummer = gammelSekvensnummer + 1,
)

internal fun DtoVedtak.toJson() = Vedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun DtoMeldeperiode.toJson() = Meldeperiode(
    dager = dager.map(DtoDag::toJson)
)

internal fun DtoDag.toJson() = Dag(
    dato = dato,
    arbeidstimer = arbeidstimer,
    type = type
)

internal fun DtoUtbetalingstidslinje.toJson() = Utbetalingstidslinje(
    dager = dager.map(DtoUtbetalingstidslinjedag::toJson)
)

internal fun DtoUtbetalingstidslinjedag.toJson() = Utbetalingstidslinjedag(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse,
    dagsats = dagsats,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg,
    høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun DtoOppdrag.toJson() = Oppdrag(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(DtoUtbetalingslinje::toJson),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

internal fun DtoUtbetalingslinje.toJson() = Utbetalingslinje(
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

internal fun DtoBarna.toJson() = Barna(
    barn = barn.map(DtoBarn::toJson)
)

internal fun DtoBarn.toJson() = Barn(
    fødselsdato = fødselsdato
)
