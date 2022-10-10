package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Barnetillegg.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.utbetaling.dto.DtoBarn
import no.nav.aap.domene.utbetaling.dto.DtoBarna
import no.nav.aap.domene.utbetaling.entitet.Beløp
import no.nav.aap.domene.utbetaling.entitet.Beløp.Companion.beløp
import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import java.time.LocalDate

internal class Barnetillegg private constructor(
    historikk: List<Barna>
) {
    internal constructor() : this(emptyList())

    private val historikk = historikk.toMutableList()
    private val nyesteInnslag get() = historikk.last()

    internal companion object {
        private val BARNETILLEGG = 27.beløp

        internal fun gjenopprett(dtoBarna: List<DtoBarna>) =
            Barnetillegg(
                historikk = dtoBarna.map(Barna::gjenopprett)
            )
    }

    internal fun barnetilleggForDag(dato: LocalDate): Beløp {
        if (historikk.isEmpty()) throw RuntimeException("Har ingen informasjon om barn")
        return BARNETILLEGG * nyesteInnslag.antallBarnUnder18År(dato)
    }

    internal fun leggTilBarn(barna: List<Barn>) {
        historikk.add(Barna(barna))
    }

    internal class Barna(
        private val barn: List<Barn>
    ) {

        internal companion object {
            internal fun gjenopprett(dtoBarna: DtoBarna) =
                Barna(
                    barn = dtoBarna.barn.map(Barn::gjenopprett)
                )
        }

        internal fun antallBarnUnder18År(dato: LocalDate) = barn.antallBarnUnder18År(dato)

        internal fun toDto() = DtoBarna(
            barn = barn.map(Barn::toDto)
        )
    }

    internal class Barn(
        private val fødselsdato: Fødselsdato
        // Legg til inntekt
    ) {
        internal companion object {
            internal fun Iterable<Barn>.antallBarnUnder18År(dato: LocalDate) =
                count { it.fødselsdato.erUnder18År(dato) }

            internal fun gjenopprett(dtoBarn: DtoBarn) =
                Barn(
                    fødselsdato = Fødselsdato(dtoBarn.fødselsdato)
                )
        }

        internal fun toDto() = DtoBarn(
            fødselsdato = fødselsdato.toDto()
        )
    }

    internal fun toDto() = historikk.map(Barna::toDto)
}
