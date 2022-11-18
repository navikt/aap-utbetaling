package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp

private const val ANTALL_DAGER_MED_UTBETALING_PER_ÅR = 260

internal class Paragraf_11_20_2_ledd_2_punktum private constructor(
    private val antallDagerMedUtbetalingPerÅr: Int,
    private val årligYtelse: Paragraf_11_20_1_ledd,
    private val dagsats: Beløp
) {

    internal constructor(årligYtelse: Paragraf_11_20_1_ledd) : this(
        antallDagerMedUtbetalingPerÅr = ANTALL_DAGER_MED_UTBETALING_PER_ÅR,
        årligYtelse = årligYtelse,
        dagsats = årligYtelse / ANTALL_DAGER_MED_UTBETALING_PER_ÅR
    )

    internal operator fun plus(barnetillegg: Beløp) = dagsats + barnetillegg
    fun toDto(): Paragraf_11_20_2_ledd_2_punktum_ModellAPI {
        return Paragraf_11_20_2_ledd_2_punktum_ModellAPI(
            antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
            årligYtelse = årligYtelse.toDto().årligytelse,
            dagsats = dagsats.toDto()
        )

    }

    companion object {
        fun gjenopprett(antallDagerMedUtbetalingPerÅr: Int, årligYtelse: Paragraf_11_20_1_ledd, dagsats: Beløp) =
            Paragraf_11_20_2_ledd_2_punktum(
                antallDagerMedUtbetalingPerÅr = antallDagerMedUtbetalingPerÅr,
                årligYtelse = årligYtelse,
                dagsats = dagsats
            )
    }

}
