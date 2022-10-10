package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto
import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto.*
import no.nav.aap.dto.kafka.MottakereKafkaDto

internal fun ForrigeMottakereKafkaDto.toDto() = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(Vedtak::toDto),
    aktivitetstidslinje = aktivitetstidslinje.map(Meldeperiode::toDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(Utbetalingstidslinje::toDto),
    oppdragshistorikk = oppdragshistorikk.map(Oppdrag::toDto),
    barnetillegg = barnetillegg.map(Barna::toDto),
    tilstand = tilstand,
)

internal fun Vedtak.toDto() = MottakereKafkaDto.Vedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun Meldeperiode.toDto() = MottakereKafkaDto.Meldeperiode(
    dager = dager.map(Dag::toDto)
)

internal fun Dag.toDto() = MottakereKafkaDto.Dag(
    dato = dato,
    arbeidstimer = arbeidstimer,
    type = type
)

internal fun Utbetalingstidslinje.toDto() = MottakereKafkaDto.Utbetalingstidslinje(
    dager = dager.map(Utbetalingstidslinjedag::toDto)
)

internal fun Utbetalingstidslinjedag.toDto() = MottakereKafkaDto.Utbetalingstidslinjedag(
    type = type,
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse,
    dagsats = dagsats,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg,
    høyestebeløpMedBarnetillegg = høyestebeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = dagsatsMedBarnetillegg,
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Oppdrag.toDto() = MottakereKafkaDto.Oppdrag(
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

internal fun Utbetalingslinje.toDto() = MottakereKafkaDto.Utbetalingslinje(
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

internal fun Barna.toDto() = MottakereKafkaDto.Barna(
    barn = barn.map(Barn::toDto)
)

internal fun Barn.toDto() = MottakereKafkaDto.Barn(
    fødselsdato = fødselsdato
)
