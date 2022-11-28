package no.nav.aap.domene.utbetaling.entitet

import java.time.LocalDate

internal class Fødselsdato(private val dato: LocalDate) {
    private val `18ÅrsDagen`: LocalDate = this.dato.plusYears(18)
    private val `25ÅrsDagen`: LocalDate = this.dato.plusYears(25)

    private companion object {
        private val MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR = Grunnlagsfaktor(2.0 / .66)
        private val MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR = Grunnlagsfaktor(4.0 / 3 / .66)
    }

    internal fun erUnder18År(dato: LocalDate) = dato < `18ÅrsDagen`

    internal fun justerGrunnlagsfaktorForAlder(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor): Grunnlagsfaktor =
        when {
            grunnlagsfaktor >= MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR -> grunnlagsfaktor
            dato >= `25ÅrsDagen` -> MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR
            grunnlagsfaktor >= MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR -> grunnlagsfaktor
            else -> MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR
        }

    internal fun toDto() = dato
}
