package no.nav.aap.app.kafka

import no.nav.aap.domene.utbetaling.modellapi.*
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
    dager = dager.mapNotNull(DagKafkaDto::toModellApi)
)

internal fun DagKafkaDto.toModellApi() =
    helgedag?.toModellApi() ?: arbeidsdag?.toModellApi() ?: fraværsdag?.toModellApi()

internal fun DagKafkaDto.HelgedagKafkaDto.toModellApi() = DagModellApi.HelgedagModellApi(
    dato = dato,
    arbeidstimer = arbeidstimer,
)

internal fun DagKafkaDto.ArbeidsdagKafkaDto.toModellApi() = DagModellApi.ArbeidsdagModellApi(
    dato = dato,
    arbeidstimer = arbeidstimer,
)

internal fun DagKafkaDto.FraværsdagKafkaDto.toModellApi() = DagModellApi.FraværsdagModellApi(
    dato = dato,
)

internal fun UtbetalingstidslinjeKafkaDto.toModellApi() = UtbetalingstidslinjeModellApi(
    dager = dager.mapNotNull(UtbetalingstidslinjedagKafkaDto::toModellApi)
)

internal fun UtbetalingstidslinjedagKafkaDto.toModellApi() =
    utbetalingsdag?.toModellApi() ?: ikkeUtbetalingsdag?.toModellApi()

internal fun UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto.toModellApi() =
    UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi(
        dato = dato,
        fødselsdato = fødselsdato,
        grunnlagsfaktor = grunnlagsfaktor,
        grunnlagsfaktorJustertForAlder = grunnlagsfaktorJustertForAlder,
        barnetillegg = barnetillegg,
        grunnlag = grunnlag.toModellApi(),
        årligYtelse = årligYtelse.toModellApi(),
        dagsats = dagsats.toModellApi(),
        høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg.toModellApi(),
        høyesteBeløpMedBarnetillegg = høyesteBeløpMedBarnetillegg.toModellApi(),
        dagsatsMedBarnetillegg = dagsatsMedBarnetillegg.toModellApi(),
        beløpMedBarnetillegg = beløpMedBarnetillegg,
        beløp = beløp,
        arbeidsprosent = arbeidsprosent,
    )

internal fun UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto.toModellApi() =
    UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi(
        dato = dato,
        arbeidsprosent = arbeidsprosent,
    )

internal fun Paragraf_11_19_3_leddKafkaDto.toModellApi() = Paragraf_11_19_3_leddModellApi(
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    grunnbeløp = grunnbeløp,
    grunnlag = grunnlag
)

internal fun Paragraf_11_20_2_ledd_2_punktum_KafkaDto.toModellApi() = Paragraf_11_20_2_ledd_2_punktum_ModellApi(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

internal fun Paragraf_11_20_1_ledd_KafkaDto.toModellApi() = Paragraf_11_20_1_ledd_ModellApi(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    grunnlag = grunnlag,
    årligytelse = årligytelse
)

internal fun Paragraf_11_20_3_5_ledd_KafkaDto.toModellApi() = Paragraf_11_20_3_5_ledd_ModellApi(
    dagsats = dagsats,
    barnetillegg = barnetillegg,
    beløp = beløp
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

internal fun MottakerModellApi.toKafkaDto(gammelSekvensnummer: Long) = MottakereKafkaDto(
    personident = personident,
    fødselsdato = fødselsdato,
    vedtakshistorikk = vedtakshistorikk.map(VedtakModellApi::toKafkaDto),
    aktivitetstidslinje = aktivitetstidslinje.map(MeldeperiodeModellApi::toKafkaDto),
    utbetalingstidslinjehistorikk = utbetalingstidslinjehistorikk.map(UtbetalingstidslinjeModellApi::toKafkaDto),
    oppdragshistorikk = oppdragshistorikk.map(OppdragModellApi::toKafkaDto),
    barnetillegg = barnetillegg.map(BarnaModellApi::toKafkaDto),
    tilstand = tilstand,
    sekvensnummer = gammelSekvensnummer + 1,
)

internal fun VedtakModellApi.toKafkaDto() = VedtakKafkaDto(
    vedtaksid = vedtaksid,
    innvilget = innvilget,
    grunnlagsfaktor = grunnlagsfaktor,
    vedtaksdato = vedtaksdato,
    virkningsdato = virkningsdato,
    fødselsdato = fødselsdato,
)

internal fun MeldeperiodeModellApi.toKafkaDto() = MeldeperiodeKafkaDto(
    dager = dager.map(DagModellApi::toKafkaDto)
)

internal fun DagModellApi.toKafkaDto() = object : DagModellApiVisitor {
    lateinit var dag: DagKafkaDto

    init {
        accept(this)
    }

    override fun visitHelgedag(helgedag: DagModellApi.HelgedagModellApi) {
        dag = helgedag.toKafkaDto()
    }

    override fun visitArbeidsdag(arbeidsdag: DagModellApi.ArbeidsdagModellApi) {
        dag = arbeidsdag.toKafkaDto()
    }

    override fun visitFraværsdag(fraværsdag: DagModellApi.FraværsdagModellApi) {
        dag = fraværsdag.toKafkaDto()
    }
}.dag

internal fun DagModellApi.HelgedagModellApi.toKafkaDto() = DagKafkaDto(
    helgedag = DagKafkaDto.HelgedagKafkaDto(
        dato = dato,
        arbeidstimer = arbeidstimer,
    ),
    arbeidsdag = null,
    fraværsdag = null,
)

internal fun DagModellApi.ArbeidsdagModellApi.toKafkaDto() = DagKafkaDto(
    helgedag = null,
    arbeidsdag = DagKafkaDto.ArbeidsdagKafkaDto(
        dato = dato,
        arbeidstimer = arbeidstimer,
    ),
    fraværsdag = null,
)

internal fun DagModellApi.FraværsdagModellApi.toKafkaDto() = DagKafkaDto(
    helgedag = null,
    arbeidsdag = null,
    fraværsdag = DagKafkaDto.FraværsdagKafkaDto(
        dato = dato,
    ),
)

internal fun UtbetalingstidslinjeModellApi.toKafkaDto() = UtbetalingstidslinjeKafkaDto(
    dager = dager.map(UtbetalingstidslinjedagModellApi::toKafkaDto)
)

internal fun UtbetalingstidslinjedagModellApi.toKafkaDto() = object : UtbetalingstidslinjedagModellApiVisitor {
    lateinit var dag: UtbetalingstidslinjedagKafkaDto

    init {
        accept(this)
    }

    override fun visitUtbetalingsdag(utbetalingsdag: UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi) {
        dag = utbetalingsdag.toKafkaDto()
    }

    override fun visitIkkeUtbetalingsdag(ikkeUtbetalingsdag: UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi) {
        dag = ikkeUtbetalingsdag.toKafkaDto()
    }
}.dag

internal fun UtbetalingstidslinjedagModellApi.UtbetalingsdagModellApi.toKafkaDto() =
    UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = UtbetalingstidslinjedagKafkaDto.UtbetalingsdagKafkaDto(
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
        ),
        ikkeUtbetalingsdag = null,
    )

internal fun UtbetalingstidslinjedagModellApi.IkkeUtbetalingsdagModellApi.toKafkaDto() =
    UtbetalingstidslinjedagKafkaDto(
        utbetalingsdag = null,
        ikkeUtbetalingsdag = UtbetalingstidslinjedagKafkaDto.IkkeUtbetalingsdagKafkaDto(
            dato = dato,
            arbeidsprosent = arbeidsprosent,
        ),
    )

internal fun Paragraf_11_19_3_leddModellApi.toKafkaDto() = Paragraf_11_19_3_leddKafkaDto(
    dato = dato,
    grunnlagsfaktor = grunnlagsfaktor,
    grunnbeløp = grunnbeløp,
    grunnlag = grunnlag
)

internal fun Paragraf_11_20_2_ledd_2_punktum_ModellApi.toKafkaDto() = Paragraf_11_20_2_ledd_2_punktum_KafkaDto(
    antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
    årligYtelse = årligYtelse,
    dagsats = dagsats
)

internal fun Paragraf_11_20_1_ledd_ModellApi.toKafkaDto() = Paragraf_11_20_1_ledd_KafkaDto(
    faktorForReduksjonAvGrunnlag = faktorForReduksjonAvGrunnlag,
    grunnlag = grunnlag,
    årligytelse = årligytelse
)

internal fun Paragraf_11_20_3_5_ledd_ModellApi.toKafkaDto() = Paragraf_11_20_3_5_ledd_KafkaDto(
    dagsats = dagsats,
    barnetillegg = barnetillegg,
    beløp = beløp
)

internal fun Paragraf_11_20_6_leddModellApi.toKafkaDto() = Paragraf_11_20_6_leddKafkaDto(
    maksFaktorAvGrunnlag = maksFaktorAvGrunnlag,
    grunnlag = grunnlag,
    høyesteÅrligYtelseMedBarnetillegg = høyesteÅrligYtelseMedBarnetillegg
)

internal fun OppdragModellApi.toKafkaDto() = OppdragKafkaDto(
    mottaker = mottaker,
    fagområde = fagområde,
    linjer = linjer.map(UtbetalingslinjeModellApi::toKafkaDto),
    fagsystemId = fagsystemId,
    endringskode = endringskode,
    nettoBeløp = nettoBeløp,
    overføringstidspunkt = overføringstidspunkt,
    avstemmingsnøkkel = avstemmingsnøkkel,
    status = status,
    tidsstempel = tidsstempel,
)

internal fun UtbetalingslinjeModellApi.toKafkaDto() = UtbetalingslinjeKafkaDto(
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

internal fun BarnaModellApi.toKafkaDto() = BarnaKafkaDto(
    barn = barn.map(BarnModellApi::toKafkaDto)
)

internal fun BarnModellApi.toKafkaDto() = BarnKafkaDto(
    fødselsdato = fødselsdato
)
