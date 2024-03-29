package no.nav.aap.domene.utbetaling.utbetalingslinjer

internal enum class Klassekode(internal val verdi: String) {
    //TODO: Hvilken klassekode skal brukes for AAP?
    AAP(verdi = "Hva skal brukes for AAP?"),
    RefusjonIkkeOpplysningspliktig(verdi = "SPREFAG-IOP"),
    RefusjonFeriepengerIkkeOpplysningspliktig(verdi = "SPREFAGFER-IOP"),
    SykepengerArbeidstakerOrdinær(verdi = "SPATORD");

    internal companion object {
        private val map = values().associateBy(Klassekode::verdi)
        fun from(verdi: String) = requireNotNull(map[verdi]) { "Støtter ikke klassekode: $verdi" }
    }
}
