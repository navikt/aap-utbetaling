package no.nav.aap.domene.utbetaling.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class DtoOppdrag(
    val mottaker: String,
    val fagområde: String,
    val linjer: List<DtoUtbetalingslinje>,
    val fagsystemId: String,
    val endringskode: String,
    val nettoBeløp: Int,
    val overføringstidspunkt: LocalDateTime?,
    val avstemmingsnøkkel: Long?,
    val status: String?,
    val tidsstempel: LocalDateTime
)

data class DtoUtbetalingslinje(
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
