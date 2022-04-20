package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.tidslinje.Dag
import java.time.LocalDate


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
internal fun Int.A(grunnlagsfaktor: Number = 3, barnetillegg: Number = 0, arbeidstimer: Number = 7.5) = (1..this)
    .map { Dag.Arbeidsdag(seed, Grunnlagsfaktor(grunnlagsfaktor), barnetillegg.beløp, arbeidstimer.toDouble()) }

internal val Int.H get() = H()
internal fun Int.H(arbeidstimer: Number = 0) = (1..this)
    .map { Dag.Helg(seed, arbeidstimer.toDouble()) }

internal val Int.V get() = V()
internal fun Int.V(grunnlagsfaktor: Number = 3, barnetillegg: Number = 0) = (1..this)
    .map { Dag.Ventedag(seed, Grunnlagsfaktor(grunnlagsfaktor), barnetillegg.beløp) }

internal val Int.F get() = F()
internal fun Int.F(grunnlagsfaktor: Number = 3, barnetillegg: Number = 0) = (1 .. this)
    .map { Dag.Fraværsdag(seed, Grunnlagsfaktor(grunnlagsfaktor), barnetillegg.beløp) }