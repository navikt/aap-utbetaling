package no.nav.aap.domene.tidslinje

import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*

data class DtoSøker(
    val personident: String,
    val fødselsdato: LocalDate
)

data class DtoVedtak(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val inntektsgrunnlag: DtoInntektsgrunnlag,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate
)

data class DtoInntektsgrunnlag(
    val beregningsdato: LocalDate,
    val inntekterSiste3Kalenderår: List<DtoInntektsgrunnlagForÅr>,
    val yrkesskade: DtoYrkesskade?,
    val fødselsdato: LocalDate,
    val sisteKalenderår: Year,
    val grunnlagsfaktor: Double
)

data class DtoInntektsgrunnlagForÅr(
    val år: Year,
    val beløpFørJustering: Double,
    val beløpJustertFor6G: Double,
    val erBeløpJustertFor6G: Boolean,
    val grunnlagsfaktor: Double
)

data class DtoYrkesskade(
    val gradAvNedsattArbeidsevneKnyttetTilYrkesskade: Double,
    val inntektsgrunnlag: DtoInntektsgrunnlagForÅr
)

data class DtoInntekt(
    val arbeidsgiver: String,
    val inntekstmåned: YearMonth,
    val beløp: Double
)

data class DtoInntekter(
    val inntekter: List<DtoInntekt>
)
