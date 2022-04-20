package no.nav.aap.domene.utbetaling.entitet

internal class Arbeidstimer(arbeidstimer: Number) : Comparable<Arbeidstimer> {

    private val arbeidstimer: Double = arbeidstimer.toDouble()

    companion object {
        internal fun Iterable<Arbeidstimer>.summer() = sumOf { it.arbeidstimer }.arbeidstimer
        val Number.arbeidstimer get() = Arbeidstimer(this)
    }

    internal operator fun div(nevner: Arbeidstimer): Double = this.arbeidstimer / nevner.arbeidstimer

    override fun compareTo(other: Arbeidstimer) = this.arbeidstimer.compareTo(other.arbeidstimer)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Arbeidstimer

        if (arbeidstimer != other.arbeidstimer) return false

        return true
    }

    override fun hashCode() = arbeidstimer.hashCode()
}
