package no.nav.aap.domene.utbetaling.aktivitetstidslinje

import no.nav.aap.domene.utbetaling.Aktivitetsdager.A
import no.nav.aap.domene.utbetaling.Aktivitetsdager.F
import no.nav.aap.domene.utbetaling.Aktivitetsdager.H
import no.nav.aap.domene.utbetaling.Aktivitetsdager.V
import no.nav.aap.domene.utbetaling.Aktivitetsdager.resetSeed
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.summerArbeidstimer
import no.nav.aap.domene.utbetaling.aktivitetstidslinje.Dag.Companion.summerNormalArbeidstimer
import no.nav.aap.domene.utbetaling.entitet.Arbeidstimer.Companion.arbeidstimer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DagTest {

    @BeforeEach
    fun beforeEach() {
        resetSeed()
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
}
