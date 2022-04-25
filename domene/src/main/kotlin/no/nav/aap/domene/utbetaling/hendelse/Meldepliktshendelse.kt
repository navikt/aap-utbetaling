package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Meldeperiode
import java.time.LocalDate

internal class Meldepliktshendelse(
    private val brukersAktivitet: List<BrukeraktivitetPerDag>
) : Hendelse() {

    internal fun populerMeldeperiode(meldeperiode: Meldeperiode, grunnlagsfaktor: Grunnlagsfaktor) {
        brukersAktivitet.forEach { it.oppdaterTidslinje(meldeperiode, grunnlagsfaktor) }
    }

}

internal class BrukeraktivitetPerDag(
    private val dato: LocalDate,
    private val arbeidstimer: Arbeidstimer,
    private val fraværsdag: Boolean
) {
    internal fun oppdaterTidslinje(meldeperiode: Meldeperiode, grunnlagsfaktor: Grunnlagsfaktor) {
        val dag = when {
            fraværsdag -> Dag.fraværsdag(dato, grunnlagsfaktor)
            else -> Dag.arbeidsdag(dato, grunnlagsfaktor, arbeidstimer)
        }
        meldeperiode.leggTilDag(dag)
    }
}
