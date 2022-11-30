package no.nav.aap.domene.utbetaling.utbetalingstidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.modellapi.Paragraf_11_20_3_5_ledd_ModellApi

internal class Paragraf_11_20_3_5_ledd private constructor(
    private val dagsats: Beløp,
    private val barnetillegg: Beløp,
    private val beløp: Beløp,
) {

    internal constructor(dagsats: Beløp, barnetillegg: Beløp) : this(
        dagsats = dagsats,
        barnetillegg = barnetillegg,
        beløp = dagsats + barnetillegg,
    )

    internal fun begrensTil(høyesteBeløpMedBarnetillegg: Paragraf_11_20_2_ledd_2_punktum) = høyesteBeløpMedBarnetillegg.begrensTil(beløp)

    internal fun toModellApi() = Paragraf_11_20_3_5_ledd_ModellApi(
        dagsats = dagsats.toModellApi(),
        barnetillegg = barnetillegg.toModellApi(),
        beløp = beløp.toModellApi(),
    )

    internal companion object {
        internal fun gjenopprett(
            dagsats: Beløp,
            barnetillegg: Beløp,
            beløp: Beløp,
        ) = Paragraf_11_20_3_5_ledd(
            dagsats = dagsats,
            barnetillegg = barnetillegg,
            beløp = beløp,
        )
    }
}
