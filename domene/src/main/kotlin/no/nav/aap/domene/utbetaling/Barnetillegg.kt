package no.nav.aap.domene.utbetaling

import no.nav.aap.domene.utbetaling.Barnetillegg.Barn.Companion.antallBarnUnder18År
import no.nav.aap.domene.utbetaling.modellapi.BarnModellApi
import no.nav.aap.domene.utbetaling.modellapi.BarnaModellApi
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

        internal fun gjenopprett(barnaModellApi: List<BarnaModellApi>) =
            Barnetillegg(
                historikk = barnaModellApi.map(Barna::gjenopprett)
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
            internal fun gjenopprett(barnaModellApi: BarnaModellApi) =
                Barna(
                    barn = barnaModellApi.barn.map(Barn::gjenopprett)
                )
        }

        internal fun antallBarnUnder18År(dato: LocalDate) = barn.antallBarnUnder18År(dato)

        internal fun toModellApi() = BarnaModellApi(
            barn = barn.map(Barn::toModellApi)
        )
    }

    internal class Barn(
        private val fødselsdato: Fødselsdato
        // Legg til inntekt
    ) {
        internal companion object {
            internal fun Iterable<Barn>.antallBarnUnder18År(dato: LocalDate) =
                count { it.fødselsdato.erUnder18År(dato) }

            internal fun gjenopprett(barnModellApi: BarnModellApi) =
                Barn(
                    fødselsdato = Fødselsdato(barnModellApi.fødselsdato)
                )
        }

        internal fun toModellApi() = BarnModellApi(
            fødselsdato = fødselsdato.toDto()
        )
    }

    internal fun toDto() = historikk.map(Barna::toModellApi)
}
