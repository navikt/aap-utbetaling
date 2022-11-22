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

internal fun UtbetalingstidslinjedagKafkaDto.toKafkaDto() =
    if (type == "UTBETALINGSDAG") {
        MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto(
            utbetalingsdag = MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto(
                dato = dato,
                grunnlagsfaktor = grunnlagsfaktor!!,
                barnetillegg = barnetillegg!!,
                grunnlag = MottakereKafkaDto.Paragraf_11_19_3_leddKafkaDto(dato, grunnlagsfaktor!!, grunnlag!!),
                årligYtelse = MottakereKafkaDto.Paragraf_11_20_1_ledd_KafkaDto(0.66, grunnlag!!, årligYtelse!!),
                dagsats = MottakereKafkaDto.Paragraf_11_20_2_ledd_2_punktum_KafkaDto(260, årligYtelse!!, dagsats!!),
                høyesteÅrligYtelseMedBarnetillegg = MottakereKafkaDto.Paragraf_11_20_6_leddKafkaDto(0.9, grunnlag!!, høyesteÅrligYtelseMedBarnetillegg!!),
                høyesteBeløpMedBarnetillegg = høyestebeløpMedBarnetillegg!!,
                dagsatsMedBarnetillegg = dagsatsMedBarnetillegg!!,
                beløpMedBarnetillegg = beløpMedBarnetillegg!!,
                beløp = beløp!!,
                arbeidsprosent = arbeidsprosent,
            ),
            ikkeUtbetalingsdag = null,
        )
    } else {
        MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto(
            utbetalingsdag = null,
            ikkeUtbetalingsdag = MottakereKafkaDto.UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto(
                dato = dato,
                arbeidsprosent = arbeidsprosent,
            )
        )
    }

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
