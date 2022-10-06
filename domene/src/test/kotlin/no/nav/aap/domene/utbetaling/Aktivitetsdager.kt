package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import java.time.LocalDate

internal object Aktivitetsdager {
    private var seed = 3 januar 2022
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

    internal val Int.F get() = (1..this)
        .map { Dag.Fraværsdag(seed) }
}
