package no.nav.aap.domene.utbetaling.tidslinje

import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import no.nav.aap.domene.utbetaling.visitor.SøkerVisitor
import java.time.LocalDate

internal class Tidslinje {
    private val dager = mutableListOf<Dag>()

    internal fun leggTilDag(dato: LocalDate, grunnlagsfaktor: Grunnlagsfaktor, barnetillegg: Beløp) {
        dager.add(Dag.opprettDag(dato, grunnlagsfaktor, barnetillegg))
    }

    internal fun accept(visitor: SøkerVisitor) {
        visitor.visitTidslinje(dager)
    }

}
