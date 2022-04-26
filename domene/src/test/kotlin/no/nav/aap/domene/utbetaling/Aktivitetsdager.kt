package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import java.time.LocalDate

object Aktivitetsdager {
    internal var seed = 3 januar 2022
        get() {
            val f = field
            field = field.plusDays(1)
            return f
        }

    internal fun resetSeed(dato: LocalDate = 3 januar 2022) {
        seed = dato
    }

    internal val Int.A get() = A()
    internal fun Int.A(arbeidstimer: Number = 7.5) = (1..this)
        .map { Dag.Arbeidsdag(seed, arbeidstimer.arbeidstimer) }

    internal val Int.H get() = H()
    internal fun Int.H(arbeidstimer: Number = 0) = (1..this)
        .map { Dag.Helg(seed, arbeidstimer.arbeidstimer) }

    internal val Int.V get() = (1..this)
        .map { Dag.Ventedag(seed) }

    internal val Int.F get() = (1..this)
        .map { Dag.Fraværsdag(seed) }
}
