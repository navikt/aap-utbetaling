package no.nav.aap.domene.utbetaling.visitor

import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import java.time.LocalDate

internal interface DagVisitor {
    fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {}
    fun visitArbeidsdag(dato: LocalDate, arbeidstimer: Arbeidstimer) {}
    fun visitFraværsdag(fraværsdag: Dag.Fraværsdag, dato: LocalDate) {}
}