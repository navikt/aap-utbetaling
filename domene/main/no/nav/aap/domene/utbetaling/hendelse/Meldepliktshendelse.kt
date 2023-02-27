package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Meldeperiode
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import java.time.LocalDate

internal class Meldepliktshendelse internal constructor(
    private val brukersAktivitet: List<BrukeraktivitetPerDag>
) : Hendelse() {

    internal fun populerMeldeperiode(meldeperiode: Meldeperiode) {
        brukersAktivitet.forEach { it.oppdaterTidslinje(meldeperiode) }
    }
}

internal class BrukeraktivitetPerDag(
    private val dato: LocalDate,
    private val arbeidstimer: Arbeidstimer,
    private val fraværsdag: Boolean
) {

    internal fun oppdaterTidslinje(meldeperiode: Meldeperiode) {
        val dag = when {
            fraværsdag -> Dag.fraværsdag(dato)
            else -> Dag.arbeidsdag(dato, arbeidstimer)
        }
        meldeperiode.leggTilDag(dag)
    }
}
