package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BarnetilleggTest {

    @Test
    fun `Hvis ingen historikk om barn finnes, kastes feil ved henting av barnetillegg for dag`() {
        assertThrows<RuntimeException> { Barnetillegg().barnetilleggForDag(1 januar 2022) }
    }

    @Test
    fun `Hvis det ikke finnes noen barn, er barnetillegget 0`() {
        val barnetillegg = Barnetillegg()
        barnetillegg.leggTilBarn(emptyList())
        assertEquals(0.beløp, barnetillegg.barnetilleggForDag(1 januar 2022))
    }

    @Test
    fun `Hvis det finnes et barn under 18 år, er barnetillegget 27`() {
        val barnetillegg = Barnetillegg()
        barnetillegg.leggTilBarn(listOf(Barnetillegg.Barn(Fødselsdato(2 januar 2004))))
        assertEquals(27.beløp, barnetillegg.barnetilleggForDag(1 januar 2022))
    }

    @Test
    fun `Hvis det finnes et barn på 18 år, er barnetillegget 0`() {
        val barnetillegg = Barnetillegg()
        barnetillegg.leggTilBarn(listOf(Barnetillegg.Barn(Fødselsdato(1 januar 2004))))
        assertEquals(0.beløp, barnetillegg.barnetilleggForDag(1 januar 2022))
    }

    @Test
    fun `Hvis det finnes et barn over 18 år, er barnetillegget 0`() {
        val barnetillegg = Barnetillegg()
        barnetillegg.leggTilBarn(listOf(Barnetillegg.Barn(Fødselsdato(31 desember 2003))))
        assertEquals(0.beløp, barnetillegg.barnetilleggForDag(1 januar 2022))
    }

    @Test
    fun `Hvis det finnes to barn under 18 år, er barnetillegget 54`() {
        val barnetillegg = Barnetillegg()
        barnetillegg.leggTilBarn(
            listOf(
                Barnetillegg.Barn(Fødselsdato(2 januar 2004)),
                Barnetillegg.Barn(Fødselsdato(2 januar 2004))
            )
        )
        assertEquals(54.beløp, barnetillegg.barnetilleggForDag(1 januar 2022))
    }

    @Test
    fun `Nye barn oppdater historikk`() {
        val barnetillegg = Barnetillegg()
        barnetillegg.leggTilBarn(
            listOf(
                Barnetillegg.Barn(Fødselsdato(2 januar 2004))
            )
        )
        val barnetilleggFørsteGang = barnetillegg.barnetilleggForDag(1 januar 2022)
        assertEquals(27.beløp, barnetilleggFørsteGang)

        barnetillegg.leggTilBarn(
            listOf(
                Barnetillegg.Barn(Fødselsdato(2 januar 2004)),
                Barnetillegg.Barn(Fødselsdato(2 januar 2004))
            )
        )
        val barnetilleggAndreGang = barnetillegg.barnetilleggForDag(1 januar 2022)
        assertEquals(54.beløp, barnetilleggAndreGang)
    }
}
