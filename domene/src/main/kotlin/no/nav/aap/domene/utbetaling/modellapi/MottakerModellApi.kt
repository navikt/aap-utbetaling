package no.nav.aap.domene.utbetaling.modellapi

import no.nav.aap.domene.utbetaling.Mottaker
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Personident
import java.time.LocalDate
import java.util.*

data class MottakerModellApi(
    val personident: String,
    val fødselsdato: LocalDate,
    val vedtakshistorikk: List<VedtakModellApi>,
    val aktivitetstidslinje: List<MeldeperiodeModellApi>,
    val utbetalingstidslinjehistorikk: List<UtbetalingstidslinjeModellApi>,
    val oppdragshistorikk: List<OppdragModellApi>,
    val barnetillegg: List<BarnaModellApi>,
    val tilstand: String
) {
    companion object {
        fun opprettMottaker(personident: String, fødselsdato: LocalDate): MottakerModellApi =
            Mottaker(Personident(personident), Fødselsdato(fødselsdato)).toModellApi()
    }
}

data class MeldeperiodeModellApi(
    val dager: List<DagModellApi>
)

interface DagModellApiVisitor {
    fun visitHelgedag(helgedag: DagModellApi.HelgedagModellApi)
    fun visitArbeidsdag(arbeidsdag: DagModellApi.ArbeidsdagModellApi)
    fun visitFraværsdag(fraværsdag: DagModellApi.FraværsdagModellApi)
}

sealed class DagModellApi {
    abstract fun accept(visitor: DagModellApiVisitor)
    internal abstract fun gjenopprett(): Dag

    data class HelgedagModellApi(
        val dato: LocalDate,
        val arbeidstimer: Double,
    ) : DagModellApi() {
        override fun accept(visitor: DagModellApiVisitor) {
            visitor.visitHelgedag(this)
        }

        override fun gjenopprett() = Dag.Helg.gjenopprett(this)
    }

    data class ArbeidsdagModellApi(
        val dato: LocalDate,
        val arbeidstimer: Double,
    ) : DagModellApi() {
        override fun accept(visitor: DagModellApiVisitor) {
            visitor.visitArbeidsdag(this)
        }

        override fun gjenopprett() = Dag.Arbeidsdag.gjenopprett(this)
    }

    data class FraværsdagModellApi(
        val dato: LocalDate,
    ) : DagModellApi() {
        override fun accept(visitor: DagModellApiVisitor) {
            visitor.visitFraværsdag(this)
        }

        override fun gjenopprett() = Dag.Fraværsdag.gjenopprett(this)
    }
}

data class VedtakModellApi(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Double,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: LocalDate
)
