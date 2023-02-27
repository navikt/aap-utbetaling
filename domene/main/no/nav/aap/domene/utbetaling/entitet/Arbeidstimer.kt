package no.nav.aap.domene.utbetaling.entitet

internal class Arbeidstimer(arbeidstimer: Number) : Comparable<Arbeidstimer> {

    private val arbeidstimer: Double = arbeidstimer.toDouble()

    init {
        require(this.arbeidstimer >= 0) { "Arbeidstimer må være større enn eller lik 0, er ${this.arbeidstimer}" }
    }

    internal companion object {
        internal val NORMAL_ARBEIDSTIMER = 7.5.arbeidstimer

        internal fun Iterable<Arbeidstimer>.summer() = sumOf { it.arbeidstimer }.arbeidstimer
        val Number.arbeidstimer get() = Arbeidstimer(this)
    }

    internal operator fun plus(addend: Arbeidstimer): Arbeidstimer =
        Arbeidstimer(this.arbeidstimer + addend.arbeidstimer)

    internal operator fun div(nevner: Arbeidstimer): Arbeidsprosent {
        if (nevner.arbeidstimer == 0.0) return Arbeidsprosent(0)
        return Arbeidsprosent(this.arbeidstimer / nevner.arbeidstimer)
    }

    internal fun toModellApi() = arbeidstimer

    override fun compareTo(other: Arbeidstimer) = this.arbeidstimer.compareTo(other.arbeidstimer)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Arbeidstimer

        if (arbeidstimer != other.arbeidstimer) return false

        return true
    }

    override fun hashCode() = arbeidstimer.hashCode()
    override fun toString() = "Arbeidstimer(arbeidstimer=$arbeidstimer)"
}
