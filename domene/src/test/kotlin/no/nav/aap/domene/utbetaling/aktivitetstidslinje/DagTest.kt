package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Aktivitetsdager.A
import no.nav.aap.domene.utbetaling.Aktivitetsdager.F
import no.nav.aap.domene.utbetaling.Aktivitetsdager.H
import no.nav.aap.domene.utbetaling.Aktivitetsdager.V
import no.nav.aap.domene.utbetaling.Aktivitetsdager.resetSeed
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.summerArbeidstimer
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.summerNormalArbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class DagTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats`() {
        val visitor = TestDagVisitor()
        val dager = 1.A(arbeidstimer = 0)
        dager.forEach { it.accept(visitor) }
        assertEquals(810.27.beløp, visitor.dagbeløp)
    }

    @Test
    fun `På helg er dagsats 0`() {
        val visitor = TestDagVisitor()
        val dager = 1.H
        dager.forEach { it.accept(visitor) }
        assertEquals(0.beløp, visitor.dagbeløp)
    }

    @Test
    fun `Omregner 66 prosent av grunnlaget til dagsats - dagsats økes med barnetillegg`() {
        val visitor = TestDagVisitor()
        val dager = 1.A(grunnlagsfaktor = 5, barnetillegg = 27 * 13, arbeidstimer = 0)
        dager.forEach { it.accept(visitor) }
        assertEquals(1701.45.beløp, visitor.dagbeløp)
    }

    @Test
    fun `Dagsats inkludert barnetillegg begrenses oppad til 90 prosent av grunnlaget`() {
        val visitor = TestDagVisitor()
        val dager = 1.A(grunnlagsfaktor = 2, barnetillegg = 27 * 14, arbeidstimer = 0)
        dager.forEach { it.accept(visitor) }
        assertEquals(736.61.beløp, visitor.dagbeløp)
    }

    @Test
    fun `En arbeidsdag har oversikt over antall timer jobbet`() {
        assertEquals(5.arbeidstimer, 1.A(arbeidstimer = 5).summerArbeidstimer())
    }

    @Test
    fun `En helgedag har oversikt over antall timer jobbet`() {
        assertEquals(5.arbeidstimer, 1.H(arbeidstimer = 5).summerArbeidstimer())
    }

    @Test
    fun `En fraværdag har ingen arbeidstimer`() {
        assertEquals(0.arbeidstimer, 1.F.summerArbeidstimer())
    }

    @Test
    fun `En ventedag har ingen arbeidstimer`() {
        assertEquals(0.arbeidstimer, 1.V.summerArbeidstimer())
    }

    @Test
    fun `Summer arbeidstimer over en periode`() {
        val dager = 5.A(arbeidstimer = 5) + 2.H(arbeidstimer = 1) + 2.V + 3.A(arbeidstimer = 5)
        assertEquals(42.arbeidstimer, dager.summerArbeidstimer())
    }

    @Test
    fun `En arbeidsdag bidrar med 7,5 timer til summen av normalarbeidstid`() {
        assertEquals(7.5.arbeidstimer, 1.A.summerNormalArbeidstimer())
    }

    @Test
    fun `To arbeidsdager bidrar med 15 timer til summen av normalarbeidstid`() {
        assertEquals(15.arbeidstimer, 2.A.summerNormalArbeidstimer())
    }

    @Test
    fun `En helgedag bidrar med 0 timer til summen av normalarbeidstid`() {
        assertEquals(0.arbeidstimer, 1.H.summerNormalArbeidstimer())
    }

    @Test
    fun `En ventedag bidrar med 7,5 timer til summen av normalarbeidstid`() {
        assertEquals(7.5.arbeidstimer, 1.V.summerNormalArbeidstimer())
    }

    private class TestDagVisitor : DagVisitor {
        lateinit var dagbeløp: Beløp

        override fun visitArbeidsdag(dagbeløp: Beløp, dato: LocalDate, arbeidstimer: Arbeidstimer) {
            this.dagbeløp = dagbeløp
        }

        override fun visitHelgedag(helgedag: Dag.Helg, dato: LocalDate, arbeidstimer: Arbeidstimer) {
            this.dagbeløp = 0.beløp
        }
    }
}
