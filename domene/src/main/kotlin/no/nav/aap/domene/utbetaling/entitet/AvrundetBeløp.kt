package no.nav.aap.domene.utbetaling.entitet

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import kotlin.math.roundToInt

internal class AvrundetBeløp private constructor(verdi: Number) : Comparable<AvrundetBeløp> {

    private val verdi: Int = verdi.toDouble().roundToInt()

    internal companion object {
        internal fun Iterable<AvrundetBeløp>.summerBeløp() = sumOf { it.verdi }.avrundetBeløp
        internal val Number.avrundetBeløp get() = AvrundetBeløp(this)
    }

    internal operator fun plus(addend: AvrundetBeløp) = AvrundetBeløp(this.verdi + addend.verdi)

    internal operator fun times(faktor: Number) = (verdi * faktor.toDouble()).beløp
    internal operator fun times(faktor: AvrundetBeløp) = this * faktor.verdi

    internal operator fun div(nevner: Number): Beløp = (verdi / nevner.toDouble()).beløp

    internal fun toModellApi() = verdi

    override fun compareTo(other: AvrundetBeløp) = verdi.compareTo(other.verdi)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvrundetBeløp

        if (verdi != other.verdi) return false

        return true
    }

    override fun hashCode() = verdi.hashCode()
    override fun toString() = "AvrundetBeløp($verdi)"
}
