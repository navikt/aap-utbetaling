package no.nav.aap.app.kafka

import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto
import no.nav.aap.dto.kafka.ForrigeMottakereKafkaDto.*
import no.nav.aap.dto.kafka.MottakereKafkaDto

internal fun ForrigeMottakereKafkaDto.toModellApi() = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(Vedtak::toModellApi),
    aktivitetstidslinje = aktivitetstidslinje.map(Meldeperiode::toModellApi),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(Utbetalingstidslinje::toModellApi),
    oppdragshistorikk = oppdragshistorikk.map(Oppdrag::toModellApi),
    barnetillegg = emptyList(), // barnetillegg.map(Barna::toDto),
    tilstand = tilstand,
)

internal fun Vedtak.toModellApi() = MottakereKafkaDto.Vedtak(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun Meldeperiode.toModellApi() = MottakereKafkaDto.Meldeperiode(
    dager = dager.map(Dag::toModellApi)
)

internal fun Dag.toModellApi() = MottakereKafkaDto.Dag(
    dato = dato,
    arbeidstimer = arbeidstimer,
    type = type
)

internal fun Utbetalingstidslinje.toModellApi() = MottakereKafkaDto.Utbetalingstidslinje(
    dager = dager.map(Utbetalingstidslinjedag::toModellApi)
)

internal fun Utbetalingstidslinjedag.toModellApi() = MottakereKafkaDto.Utbetalingstidslinjedag(
    type = if (grunnlagsfaktor != null) "UTBETALINGSDAG" else "IKKE_UTBETALINGSDAG",
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    barnetillegg = barnetillegg,
    grunnlag = grunnlag,
    årligYtelse = årligYtelse?.let { MottakereKafkaDto.Paragraf_11_20_1_ledd_KafkaDTO(0.66, grunnlag!!, it) },
    dagsats = dagsats?.let { MottakereKafkaDto.Paragraf_11_20_2_ledd_2_punktum_KafkaDTO(260,årligYtelse!!, it)},
    høyesteÅrligYtelseMedBarnetillegg = grunnlag?.times(.9),
    høyesteBeløpMedBarnetillegg = høyestebeløpMedBarnetillegg,
    dagsatsMedBarnetillegg = barnetillegg?.let { dagsats?.plus(it) },
    beløpMedBarnetillegg = beløpMedBarnetillegg,
    beløp = beløp,
    arbeidsprosent = arbeidsprosent,
)

internal fun Oppdrag.toModellApi() = MottakereKafkaDto.Oppdrag(
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

internal fun Utbetalingslinje.toModellApi() = MottakereKafkaDto.Utbetalingslinje(
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

internal fun Barna.toModellApi() = MottakereKafkaDto.Barna(
    barn = barn.map(Barn::toModellApi)
)

internal fun Barn.toModellApi() = MottakereKafkaDto.Barn(
    fødselsdato = fødselsdato
)
