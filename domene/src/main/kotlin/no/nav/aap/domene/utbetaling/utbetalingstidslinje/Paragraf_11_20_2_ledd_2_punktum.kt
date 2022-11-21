package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_2_ledd_2_punktum_ModellApi

internal class Paragraf_11_20_2_ledd_2_punktum private constructor(
    private val antallDagerMedUtbetalingPerÅr: Int,
    private val årligYtelse: Paragraf_11_20_1_ledd,
    //TODO: Heltall??
    private val dagsats: Beløp
) {

    internal constructor(årligYtelse: Paragraf_11_20_1_ledd) : this(
        antallDagerMedUtbetalingPerÅr = ANTALL_DAGER_MED_UTBETALING_PER_ÅR,
        årligYtelse = årligYtelse,
        dagsats = årligYtelse / ANTALL_DAGER_MED_UTBETALING_PER_ÅR
    )

    internal operator fun plus(barnetillegg: Beløp) = dagsats + barnetillegg

    internal fun toDto() = Paragraf_11_20_2_ledd_2_punktum_ModellApi(
        antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
        årligYtelse = årligYtelse.toDto().årligytelse,
        dagsats = dagsats.toDto()
    )

    internal companion object {
        private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260

        internal fun gjenopprett(
            antallDagerMedUtbetalingPerÅr: Int,
            årligYtelse: Paragraf_11_20_1_ledd,
            dagsats: Beløp
        ) = Paragraf_11_20_2_ledd_2_punktum(
            antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
            årligYtelse = årligYtelse,
            dagsats = dagsats
        )
    }
}
