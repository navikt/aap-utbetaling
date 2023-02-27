package no.nav.aap.domene.utbetaling.entitet

import no.nav.aap.domene.utbetaling.desember
import no.nav.aap.domene.utbetaling.februar
import no.nav.aap.domene.utbetaling.januar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class FødselsdatoTest {

    @Test
    fun `Er ikke under 18 år på 18-årsdagen`() {
        val `18-årsdagen` = 1.januar(2022)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertFalse(fødselsdato.erUnder18År(`18-årsdagen`))
    }

    @Test
    fun `Er under 18 år dagen før 18-årsdagen`() {
        val `18-årsdagen` = 31.desember(2021)
        val fødselsdato = Fødselsdato(1.januar(2004))

        assertTrue(fødselsdato.erUnder18År(`18-årsdagen`))
    }

    @Test
    fun `På 25-årsdagen er minimum grunnbeløp 2G - beregningsfaktor for minste årlige ytelse er 2G delt på 66 prosent`() {
        val `25-årsdagen` = 1.februar(2029)
        val fødselsdato = Fødselsdato(1.februar(2004))
        val beregningsfaktor = Grunnlagsfaktor(1.0)

        assertEquals(
            Grunnlagsfaktor(2.0 / .66),
            fødselsdato.justerGrunnlagsfaktorForAlder(`25-årsdagen`, beregningsfaktor)
        )
    }

    @Test
    fun `Dagen før 25-årsdagen er minimum grunnbeløp fire-tredjedels G - beregningsfaktor for minste årlige ytelse er fire-tredjedels G delt på 66 prosent`() {
        val `dagen før 25-årsdagen` = 31.januar(2029)
        val fødselsdato = Fødselsdato(1.februar(2004))
        val beregningsfaktor = Grunnlagsfaktor(1.0)

        assertEquals(
            Grunnlagsfaktor(4.0 / 3 / .66),
            fødselsdato.justerGrunnlagsfaktorForAlder(`dagen før 25-årsdagen`, beregningsfaktor)
        )
    }

    @Test
    fun `På 25-årsdagen returneres beregningsfaktoren hvis den er over 2G delt på 66 prosent`() {
        val `25-årsdagen` = 1 februar 2029
        val fødselsdato = Fødselsdato(1 februar 2004)
        val beregningsfaktor = Grunnlagsfaktor(3.1)

        assertEquals(Grunnlagsfaktor(3.1), fødselsdato.justerGrunnlagsfaktorForAlder(`25-årsdagen`, beregningsfaktor))
    }

    @Test
    fun `Dagen før 25-årsdagen returneres beregningsfaktoren hvis den er over fire-tredjedels G`() {
        val `dagen før 25-årsdagen` = 31.januar(2029)
        val fødselsdato = Fødselsdato(1.februar(2004))
        val beregningsfaktor = Grunnlagsfaktor(2.1)

        assertEquals(
            Grunnlagsfaktor(2.1),
            fødselsdato.justerGrunnlagsfaktorForAlder(`dagen før 25-årsdagen`, beregningsfaktor)
        )
    }
}
