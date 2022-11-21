package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.modellapi.DagModellApi
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.NORMAL_ARBEIDSTIMER
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.summer
import no.nav.aap.domene.utbetaling.visitor.DagVisitor
import java.time.DayOfWeek
import java.time.LocalDate

internal sealed class Dag(
    protected val dato: LocalDate,
) {

    internal fun sammenfallerMed(other: Dag) = this.dato == other.dato

    internal abstract fun arbeidstimer(): Arbeidstimer
    internal open fun normalArbeidstimer(): Arbeidstimer = NORMAL_ARBEIDSTIMER

    internal abstract fun accept(visitor: DagVisitor)

    internal abstract fun toDto(): DagModellApi

    private enum class Dagtype {
        HELG,
        ARBEIDSDAG,
        FRAVÆRSDAG
    }

    internal class Helg(
        dato: LocalDate,
        private val arbeidstimer: Arbeidstimer
    ) : Dag(dato) {
        override fun arbeidstimer() = arbeidstimer
        override fun normalArbeidstimer(): Arbeidstimer = 0.arbeidstimer

        override fun accept(visitor: DagVisitor) {
            visitor.visitHelgedag(this, dato, arbeidstimer)
        }

        override fun toDto() = DagModellApi(
            dato = dato,
            arbeidstimer = arbeidstimer.toDto(),
            type = Dagtype.HELG.name
        )
    }

    internal class Arbeidsdag(
        dato: LocalDate,
        private val arbeidstimer: Arbeidstimer
    ) : Dag(dato) {
        override fun arbeidstimer() = arbeidstimer

        override fun accept(visitor: DagVisitor) = visitor.visitArbeidsdag(dato, arbeidstimer)

        override fun toDto() = DagModellApi(
            dato = dato,
            arbeidstimer = arbeidstimer.toDto(),
            type = Dagtype.ARBEIDSDAG.name
        )
    }

    internal class Fraværsdag(
        dato: LocalDate
    ) : Dag(dato) {
        override fun arbeidstimer() = 0.arbeidstimer
        override fun normalArbeidstimer() = NORMAL_ARBEIDSTIMER

        override fun accept(visitor: DagVisitor) {
            visitor.visitFraværsdag(this, dato)
        }

        override fun toDto() = DagModellApi(
            dato = dato,
            arbeidstimer = null,
            type = Dagtype.FRAVÆRSDAG.name
        )
    }

    internal companion object {
        internal fun Iterable<Dag>.summerArbeidstimer() = map(Dag::arbeidstimer).summer()
        internal fun Iterable<Dag>.summerNormalArbeidstimer() = map(Dag::normalArbeidstimer).summer()

        internal fun arbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) =
            if (dato.erHelg()) Helg(dato, arbeidstimer)
            else Arbeidsdag(dato, arbeidstimer)

        internal fun fraværsdag(dato: LocalDate) =
            Fraværsdag(dato)

        internal fun gjenopprett(dagModellApi: DagModellApi) = when (enumValueOf<Dagtype>(dagModellApi.type)) {
            Dagtype.HELG -> Helg(dagModellApi.dato, Arbeidstimer(requireNotNull(dagModellApi.arbeidstimer)))
            Dagtype.ARBEIDSDAG -> Arbeidsdag(dagModellApi.dato, Arbeidstimer(requireNotNull(dagModellApi.arbeidstimer)))
            Dagtype.FRAVÆRSDAG -> Fraværsdag(dagModellApi.dato)
        }
    }
}

internal fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
