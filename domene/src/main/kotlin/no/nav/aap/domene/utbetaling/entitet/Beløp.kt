package no.nav.aap.domene.utbetaling.entitet

import no.nav.aap.domene.utbetaling.entitet.AvrundetBeløp.Companion.avrundetBeløp
import kotlin.math.round

internal class Beløp private constructor(verdi: Number) : Comparable<Beløp> {

    private val verdi: Double = round(verdi.toDouble() * 100) / 100

    internal companion object {
        internal fun Iterable<Beløp>.summerBeløp() = sumOf { it.verdi }.beløp
        internal val Number.beløp get() = Beløp(this)
    }

    internal operator fun plus(addend: Beløp) = Beløp(this.verdi + addend.verdi)

    internal operator fun times(faktor: Number) = Beløp(verdi * faktor.toDouble())
    internal operator fun times(faktor: Beløp) = this * faktor.verdi

    internal operator fun div(nevner: Number): Beløp = Beløp(verdi / nevner.toDouble())

    internal fun avrundet(): AvrundetBeløp = this.verdi.avrundetBeløp

    internal fun reduserMotArbeid(arbeidsprosent: Arbeidsprosent): Beløp = arbeidsprosent.reduserBeløpMotArbeid(verdi)

    internal fun toModellApi() = verdi

    override fun compareTo(other: Beløp) = verdi.compareTo(other.verdi)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Beløp

        if (verdi != other.verdi) return false

        return true
    }

    override fun hashCode() = verdi.hashCode()
    override fun toString() = "Beløp($verdi)"
}
