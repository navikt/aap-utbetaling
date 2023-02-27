package no.nav.aap.domene.utbetaling.utbetalingslinjer

import no.nav.aap.domene.utbetaling.januar
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

internal class OppdragTest {

    @Test
    fun `Tomt oppdrag uten linjer`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = emptyList(),
            fagsystemId = fagsystemId
        )
        assertEquals(0, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(0, inspektør.antallLinjer())
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }

    @Test
    fun `Oppdrag med en ny linje har endringskode NY`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemId,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemId
        )
        assertEquals(10, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }

    @Test
    fun `Oppdrag med to nye linjer har endringskode NY`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 13 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemId,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                ),
                Utbetalingslinje(
                    fom = 14 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 405,
                    aktuellDagsinntekt = 810,
                    grad = 50,
                    refFagsystemId = fagsystemId,
                    delytelseId = 2,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemId
        )
        assertEquals(10, oppdrag.stønadsdager())
        val inspektør = oppdrag.inspektør
        assertEquals(2, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertNull(inspektør.datoStatusFom(1))
        assertEquals(Endringskode.NY, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }

    @Test
    fun `Rekalkulert oppdrag med tom endret fremover i tid har en linje og endringskode ENDR`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 13 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemId,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemId
        )
        val fagsystemIdRekalkulert = genererUtbetalingsreferanse(UUID.randomUUID())
        val rekalkulert = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemIdRekalkulert,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemIdRekalkulert
        )
        rekalkulert.minus(oppdrag)
        assertEquals(10, rekalkulert.stønadsdager())
        val inspektør = rekalkulert.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.ENDR, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }

    @Test
    fun `Rekalkulert oppdrag med tom endret bakover i tid har en linje og endringskode ENDR`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemId,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemId
        )
        val fagsystemIdRekalkulert = genererUtbetalingsreferanse(UUID.randomUUID())
        val rekalkulert = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 13 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemIdRekalkulert,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemIdRekalkulert
        )
        rekalkulert.minus(oppdrag)
        assertEquals(9, rekalkulert.stønadsdager())
        val inspektør = rekalkulert.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.ENDR, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }

    @Test
    fun `Rekalkulert oppdrag med fom endret fremover i tid har to linjer, en av de annullert, og endringskode ENDR`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemId,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemId
        )
        val fagsystemIdRekalkulert = genererUtbetalingsreferanse(UUID.randomUUID())
        val rekalkulert = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 5 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemIdRekalkulert,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemIdRekalkulert
        )
        rekalkulert.minus(oppdrag)
        assertEquals(8, rekalkulert.stønadsdager())
        val inspektør = rekalkulert.inspektør
        assertEquals(2, inspektør.antallLinjer())
        assertEquals(3 januar 2022, inspektør.datoStatusFom(0))
        assertNull(inspektør.datoStatusFom(1))
        assertEquals(Endringskode.ENDR, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }

    @Test
    fun `Rekalkulert oppdrag med fom endret bakover i tid har to linjer, en av de annullert, og endringskode ENDR`() {
        val fagsystemId = genererUtbetalingsreferanse(UUID.randomUUID())
        val oppdrag = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 5 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemId,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemId
        )
        val fagsystemIdRekalkulert = genererUtbetalingsreferanse(UUID.randomUUID())
        val rekalkulert = Oppdrag(
            mottaker = "12345678910",
            fagområde = Fagområde.Arbeidsavklaringspenger,
            linjer = listOf(
                Utbetalingslinje(
                    fom = 3 januar 2022,
                    tom = 16 januar 2022,
                    satstype = Satstype.Daglig,
                    beløp = 810,
                    aktuellDagsinntekt = 810,
                    grad = 100,
                    refFagsystemId = fagsystemIdRekalkulert,
                    delytelseId = 1,
                    refDelytelseId = null,
                    endringskode = Endringskode.NY,
                    klassekode = Klassekode.SykepengerArbeidstakerOrdinær,
                    datoStatusFom = null
                )
            ),
            fagsystemId = fagsystemIdRekalkulert
        )
        rekalkulert.minus(oppdrag)
        assertEquals(10, rekalkulert.stønadsdager())
        val inspektør = rekalkulert.inspektør
        assertEquals(1, inspektør.antallLinjer())
        assertNull(inspektør.datoStatusFom(0))
        assertEquals(Endringskode.ENDR, inspektør.endringskode)
        assertEquals(fagsystemId, inspektør.fagsystemId())
    }
}
