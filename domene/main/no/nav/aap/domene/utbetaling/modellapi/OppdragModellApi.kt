package no.nav.aap.domene.utbetaling.modellapi

import java.time.LocalDate
import java.time.LocalDateTime

data class OppdragModellApi(
    val mottaker: String,
    val fagområde: String,
    val linjer: List<UtbetalingslinjeModellApi>,
    val fagsystemId: String,
    val endringskode: String,
    val nettoBeløp: Int,
    val overføringstidspunkt: LocalDateTime?,
    val avstemmingsnøkkel: Long?,
    val status: String?,
    val tidsstempel: LocalDateTime
)

data class UtbetalingslinjeModellApi(
    val fom: LocalDate,
    val tom: LocalDate,
    val satstype: String,
    val beløp: Int?,
    val aktuellDagsinntekt: Int?,
    val grad: Int?,
    val refFagsystemId: String?,
    val delytelseId: Int,
    val refDelytelseId: Int?,
    val endringskode: String,
    val klassekode: String,
    val datoStatusFom: LocalDate?
)
