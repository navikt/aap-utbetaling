package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.NORMAL_ARBEIDSTIMER
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.summer
import no.nav.aap.domene.utbetaling.modellapi.DagModellApi
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

    internal abstract fun toModellApi(): DagModellApi

    internal class Helg(
        dato: LocalDate,
        private val arbeidstimer: Arbeidstimer
    ) : Dag(dato) {
        override fun arbeidstimer() = arbeidstimer
        override fun normalArbeidstimer(): Arbeidstimer = 0.arbeidstimer

        override fun accept(visitor: DagVisitor) {
            visitor.visitHelgedag(this, dato, arbeidstimer)
        }

        override fun toModellApi() = DagModellApi.HelgedagModellApi(
            dato = dato,
            arbeidstimer = arbeidstimer.toModellApi(),
        )

        internal companion object {
            internal fun gjenopprett(modellApi: DagModellApi.HelgedagModellApi) = Helg(
                dato = modellApi.dato,
                arbeidstimer = Arbeidstimer(modellApi.arbeidstimer),
            )
        }
    }

    internal class Arbeidsdag(
        dato: LocalDate,
        private val arbeidstimer: Arbeidstimer
    ) : Dag(dato) {
        override fun arbeidstimer() = arbeidstimer

        override fun accept(visitor: DagVisitor) = visitor.visitArbeidsdag(dato, arbeidstimer)

        override fun toModellApi() = DagModellApi.ArbeidsdagModellApi(
            dato = dato,
            arbeidstimer = arbeidstimer.toModellApi(),
        )

        internal companion object {
            internal fun gjenopprett(modellApi: DagModellApi.ArbeidsdagModellApi) = Arbeidsdag(
                dato = modellApi.dato,
                arbeidstimer = Arbeidstimer(modellApi.arbeidstimer),
            )
        }
    }

    internal class Fraværsdag(
        dato: LocalDate
    ) : Dag(dato) {
        override fun arbeidstimer() = 0.arbeidstimer
        override fun normalArbeidstimer() = NORMAL_ARBEIDSTIMER

        override fun accept(visitor: DagVisitor) {
            visitor.visitFraværsdag(this, dato)
        }

        override fun toModellApi() = DagModellApi.FraværsdagModellApi(
            dato = dato,
        )

        internal companion object {
            internal fun gjenopprett(modellApi: DagModellApi.FraværsdagModellApi) = Fraværsdag(
                dato = modellApi.dato,
            )
        }
    }

    internal companion object {
        internal fun Iterable<Dag>.summerArbeidstimer() = map(Dag::arbeidstimer).summer()
        internal fun Iterable<Dag>.summerNormalArbeidstimer() = map(Dag::normalArbeidstimer).summer()

        internal fun arbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) =
            if (dato.erHelg()) Helg(dato, arbeidstimer)
            else Arbeidsdag(dato, arbeidstimer)

        internal fun fraværsdag(dato: LocalDate) =
            Fraværsdag(dato)
    }
}

internal fun LocalDate.erHelg() = this.dayOfWeek in arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
