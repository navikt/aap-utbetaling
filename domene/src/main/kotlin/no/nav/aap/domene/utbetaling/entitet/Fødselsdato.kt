package no.nav.aap.domene.utbetaling.entitet

import java.time.LocalDate

class Fødselsdato(private val dato: LocalDate) {
    private val `18ÅrsDagen`: LocalDate = this.dato.plusYears(18)
    private val `25ÅrsDagen`: LocalDate = this.dato.plusYears(25)

    private companion object {
        private val MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR = Grunnlagsfaktor(2.0 / .66)
        private val MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR = Grunnlagsfaktor(4.0 / 3 / .66)
    }

    internal fun erUnder18År(dato: LocalDate) = dato < `18ÅrsDagen`

    internal fun justerGrunnlagsfaktorForAlder(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor): Grunnlagsfaktor {
        val minsteGrunnlagsfaktorForAlder =
            if (dato < `25ÅrsDagen`) MINSTE_GRUNNLAGSFAKTOR_UNDER_25_ÅR else MINSTE_GRUNNLAGSFAKTOR_OVER_25_ÅR
        return maxOf(grunnlagsfaktor, minsteGrunnlagsfaktorForAlder)
    }

    internal fun toDto() = dato
}
