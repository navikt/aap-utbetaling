package no.nav.aap.domene.utbetaling.entitet

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp

internal class Arbeidsprosent(prosent: Number) : Comparable<Arbeidsprosent> {

    private val prosent: Double = prosent.toDouble()

    init {
        require(this.prosent >= 0) { "Arbeidsprosent må være større enn eller lik 0, er ${this.prosent}" }
    }

    private fun prosentIkkeArbeid() = (1 - prosent).coerceAtLeast(0.0)

    internal fun reduserBeløpMotArbeid(verdi: Double): Beløp = (verdi * prosentIkkeArbeid()).beløp

    internal fun toModellApi() = prosent
    override fun compareTo(other: Arbeidsprosent) = prosent.compareTo(other.prosent)

    override fun toString() = "Arbeidsprosent(prosent=$prosent)"
}
