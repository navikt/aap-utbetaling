package no.nav.aap.domene.utbetaling.hendelse

import no.nav.aap.domene.utbetaling.entitet.Fødselsdato
import no.nav.aap.domene.utbetaling.entitet.Grunnlagsfaktor
import java.time.LocalDate
import java.util.*

internal class Vedtakshendelse(
    val vedtaksid: UUID,
    val innvilget: Boolean,
    val grunnlagsfaktor: Grunnlagsfaktor,
    val vedtaksdato: LocalDate,
    val virkningsdato: LocalDate,
    val fødselsdato: Fødselsdato
) : Hendelse()