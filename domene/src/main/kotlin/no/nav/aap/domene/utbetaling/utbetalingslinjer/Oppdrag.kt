package no.nav.aap.domene.utbetaling.utbetalingslinjer

import no.nav.aap.domene.utbetaling.dto.DtoOppdrag
import no.nav.aap.domene.utbetaling.utbetalingslinjer.Utbetalingslinje.Companion.toDto
import no.nav.aap.domene.utbetaling.visitor.OppdragVisitor
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDate.MIN
import java.time.LocalDateTime
import java.util.*

internal const val WARN_FORLENGER_OPPHØRT_OPPDRAG =
    "Utbetalingen forlenger et tidligere oppdrag som opphørte alle utbetalte dager. Sjekk simuleringen."
internal const val WARN_OPPDRAG_FOM_ENDRET = "Utbetaling fra og med dato er endret. Kontroller simuleringen"

internal class Oppdrag private constructor(
    private val mottaker: String,
    private val fagområde: Fagområde,
    private val linjer: MutableList<Utbetalingslinje>,
    private var fagsystemId: String,
    private var endringskode: Endringskode,
    private var nettoBeløp: Int = linjer.sumOf { it.totalbeløp() },
    private var overføringstidspunkt: LocalDateTime? = null,
    private var avstemmingsnøkkel: Long? = null,
    private var status: Oppdragstatus? = null,
    private val tidsstempel: LocalDateTime
) : MutableList<Utbetalingslinje> by linjer {
    internal companion object {
        private val log = LoggerFactory.getLogger("Oppdrag")

        internal fun periode(vararg oppdrag: Oppdrag): Periode? {
            return oppdrag
                .filter(Oppdrag::isNotEmpty)
                .takeIf(List<*>::isNotEmpty)
                ?.let { liste -> Periode(liste.minOf { it.førstedato }, liste.maxOf { it.sistedato }) }
        }

        internal fun stønadsdager(vararg oppdrag: Oppdrag): Int {
            return Utbetalingslinje.stønadsdager(oppdrag.toList().flatten())
        }

        internal fun synkronisert(vararg oppdrag: Oppdrag): Boolean {
            val endrede = oppdrag.filter { it.harUtbetalinger() }
            return endrede.all { it.status == endrede.first().status }
        }

        internal fun ingenFeil(vararg oppdrag: Oppdrag) = oppdrag.none {
            it.status in listOf(
                Oppdragstatus.AVVIST,
                Oppdragstatus.FEIL
            )
        }

        internal fun kanIkkeForsøkesPåNy(vararg oppdrag: Oppdrag) = oppdrag.any { it.status == Oppdragstatus.AVVIST }
    }

    internal val førstedato get() = linjer.firstOrNull()?.let { it.datoStatusFom() ?: it.fom } ?: MIN
    internal val sistedato get() = linjer.lastOrNull()?.tom ?: MIN

    internal constructor(
        mottaker: String,
        fagområde: Fagområde,
        linjer: List<Utbetalingslinje> = listOf(),
        fagsystemId: String = genererUtbetalingsreferanse(UUID.randomUUID())
    ) : this(
        mottaker = mottaker,
        fagområde = fagområde,
        linjer = linjer.toMutableList(),
        fagsystemId = fagsystemId,
        endringskode = Endringskode.NY,
        tidsstempel = LocalDateTime.now()
    )

    internal fun accept(visitor: OppdragVisitor) {
        visitor.preVisitOppdrag(
            this,
            fagområde,
            fagsystemId,
            mottaker,
            førstedato,
            sistedato,
            stønadsdager(),
            totalbeløp(),
            nettoBeløp,
            tidsstempel,
            endringskode,
            avstemmingsnøkkel,
            status,
            overføringstidspunkt
        )
        linjer.forEach { it.accept(visitor) }
        visitor.postVisitOppdrag(
            this,
            fagområde,
            fagsystemId,
            mottaker,
            førstedato,
            sistedato,
            stønadsdager(),
            totalbeløp(),
            nettoBeløp,
            tidsstempel,
            endringskode,
            avstemmingsnøkkel,
            status,
            overføringstidspunkt
        )
    }

    internal fun fagsystemId() = fagsystemId

    private operator fun contains(other: Oppdrag) = this.tilhører(other) || this.overlapperMed(other)

    internal fun tilhører(other: Oppdrag) = this.fagsystemId == other.fagsystemId && this.fagområde == other.fagområde
    private fun overlapperMed(other: Oppdrag) =
        !erTomt() && maxOf(this.førstedato, other.førstedato) <= minOf(this.sistedato, other.sistedato)

    internal fun overfør(
        maksdato: LocalDate?,
        saksbehandler: String
    ) {
        if (!harUtbetalinger()) return log.info("Overfører ikke oppdrag uten endring for fagområde=$fagområde med fagsystemId=$fagsystemId")
        check(endringskode != Endringskode.UEND)
        check(status != Oppdragstatus.AKSEPTERT)
    }

    private fun behovdetaljer(saksbehandler: String, maksdato: LocalDate?): MutableMap<String, Any> {
        return mutableMapOf(
            "mottaker" to mottaker,
            "fagområde" to "$fagområde",
            "linjer" to kopierKunLinjerMedEndring().map(Utbetalingslinje::toHendelseMap),
            "fagsystemId" to fagsystemId,
            "endringskode" to "$endringskode",
            "saksbehandler" to saksbehandler
        ).apply {
            maksdato?.let {
                put("maksdato", maksdato.toString())
            }
        }
    }

    internal fun totalbeløp() = linjerUtenOpphør().sumOf { it.totalbeløp() }
    internal fun stønadsdager() = sumOf { it.stønadsdager() }

    internal fun nettoBeløp() = nettoBeløp

    private fun nettoBeløp(tidligere: Oppdrag) {
        nettoBeløp = this.totalbeløp() - tidligere.totalbeløp()
    }

    internal fun harUtbetalinger() = any(Utbetalingslinje::erForskjell)

    internal fun erRelevant(fagsystemId: String, fagområde: Fagområde) =
        this.fagsystemId == fagsystemId && this.fagområde == fagområde


    private fun kopierKunLinjerMedEndring() = kopierMed(filter(Utbetalingslinje::erForskjell))

    private fun kopierUtenOpphørslinjer() = kopierMed(linjerUtenOpphør())

    internal fun linjerUtenOpphør() = filter { !it.erOpphør() }

    internal fun annuller(): Oppdrag {
        return tomtOppdrag().minus(this)
    }

    private fun tomtOppdrag(): Oppdrag =
        Oppdrag(
            mottaker = mottaker,
            fagområde = fagområde,
            fagsystemId = fagsystemId
        )

    internal fun minus(eldre: Oppdrag): Oppdrag {
        // Vi ønsker ikke å forlenge et oppdrag vi ikke overlapper med, eller et tomt oppdrag
        if (harIngenKoblingTilTidligereOppdrag(eldre)) return this
        // overtar fagsystemId fra tidligere Oppdrag uten utbetaling, gitt at det er samme arbeidsgiverperiode
        if (eldre.erTomt()) return this.also { this.fagsystemId = eldre.fagsystemId }
        return when {
            // om man trekker fra et utbetalt oppdrag med et tomt oppdrag medfører det et oppdrag som opphører (les: annullerer) hele fagsystemIDen
            erTomt() -> annulleringsoppdrag(eldre)
            // "fom" kan flytte seg fremover i tid dersom man, eksempelvis, revurderer en utbetalt periode til å starte med ikke-utbetalte dager (f.eks. ferie)
            eldre.ingenUtbetalteDager() -> {
                log.warn(WARN_FORLENGER_OPPHØRT_OPPDRAG)
                kjørFrem(eldre)
            }
            fomHarFlyttetSegFremover(eldre.kopierUtenOpphørslinjer()) -> {
                log.warn("Utbetaling opphører tidligere utbetaling. Kontroller simuleringen")
                returførOgKjørFrem(eldre.kopierUtenOpphørslinjer())
            }
            // utbetaling kan endres til å starte tidligere, eksempelvis via revurdering der feriedager egentlig er sykedager
            fomHarFlyttetSegBakover(eldre.kopierUtenOpphørslinjer()) -> {
                log.warn(WARN_OPPDRAG_FOM_ENDRET)
                kjørFrem(eldre.kopierUtenOpphørslinjer())
            }
            // fom er lik, men endring kan oppstå overalt ellers
            else -> endre(eldre.kopierUtenOpphørslinjer())
        }.also { it.nettoBeløp(eldre) }
    }

    private fun harIngenKoblingTilTidligereOppdrag(eldre: Oppdrag) = this !in eldre

    private fun ingenUtbetalteDager() = linjerUtenOpphør().isEmpty()

    private fun erTomt() = this.isEmpty()

    // Vi har oppdaget utbetalingsdager tidligere i tidslinjen
    private fun fomHarFlyttetSegBakover(eldre: Oppdrag) = this.førstedato < eldre.førstedato

    // Vi har endret tidligere utbetalte dager til ikke-utbetalte dager i starten av tidslinjen
    private fun fomHarFlyttetSegFremover(eldre: Oppdrag) = this.førstedato > eldre.førstedato

    // man opphører (annullerer) et annet oppdrag ved å lage en opphørslinje som dekker hele perioden som er utbetalt
    private fun annulleringsoppdrag(tidligere: Oppdrag) = this.also { nåværende ->
        nåværende.kobleTil(tidligere)
        linjer.add(tidligere.last().opphørslinje(tidligere.first().fom))
    }

    // når man oppretter en NY linje med dato-intervall "(a, b)" vil oppdragsystemet
    // automatisk opphøre alle eventuelle linjer med fom > b.
    //
    // Eksempel:
    // Oppdrag 1: 5. januar til 31. januar (linje 1)
    // Oppdrag 2: 1. januar til 10. januar
    // Fordi linje "1. januar - 10. januar" opprettes som NY, medfører dette at oppdragsystemet opphører 11. januar til 31. januar automatisk
    private fun kjørFrem(tidligere: Oppdrag) = this.also { nåværende ->
        nåværende.kobleTil(tidligere)
        nåværende.first().kobleTil(tidligere.last())
        nåværende.zipWithNext { a, b -> b.kobleTil(a) }
    }

    private lateinit var tilstand: Tilstand
    private lateinit var sisteLinjeITidligereOppdrag: Utbetalingslinje

    private lateinit var linkTo: Utbetalingslinje

    // forsøker så langt det lar seg gjøre å endre _siste_ linje, dersom mulig *)
    // ellers lager den NY linjer fra og med linja før endringen oppstod
    // *) en linje kan endres dersom "tom"-dato eller grad er eneste forskjell
    //    ulik dagsats eller fom-dato medfører enten at linjen får status OPPH, eller at man overskriver
    //    ved å sende NY linjer
    private fun endre(avtroppendeOppdrag: Oppdrag) =
        this.also { påtroppendeOppdrag ->
            this.linkTo = avtroppendeOppdrag.last()
            påtroppendeOppdrag.kobleTil(avtroppendeOppdrag)
            påtroppendeOppdrag.kopierLikeLinjer(avtroppendeOppdrag)
            påtroppendeOppdrag.håndterLengreNåværende(avtroppendeOppdrag)
            if (!påtroppendeOppdrag.last().erForskjell()) påtroppendeOppdrag.endringskode = Endringskode.UEND
        }

    // når man oppretter en NY linje vil Oppdragsystemet IKKE ta stilling til periodene FØR.
    // Man må derfor eksplisitt opphøre evt. perioder tidligere, som i praksis vil medføre at
    // oppdraget kjøres tilbake, så fremover
    private fun returførOgKjørFrem(tidligere: Oppdrag) = this.also { nåværende ->
        val deletion = nåværende.opphørOppdrag(tidligere)
        nåværende.kjørFrem(tidligere)
        nåværende.add(0, deletion)
    }

    private fun opphørTidligereLinjeOgOpprettNy(nåværende: Utbetalingslinje, tidligere: Utbetalingslinje) {
        linkTo = tidligere
        add(this.indexOf(nåværende), tidligere.opphørslinje(tidligere.fom))
        nåværende.kobleTil(linkTo)
        tilstand = Ny()
        log.warn("Endrer tidligere oppdrag. Kontroller simuleringen.")
    }

    private fun opphørOppdrag(tidligere: Oppdrag) =
        tidligere.last().opphørslinje(tidligere.førstedato)

    private fun kopierMed(linjer: List<Utbetalingslinje>) = Oppdrag(
        mottaker = mottaker,
        fagområde = fagområde,
        linjer = linjer.toMutableList(),
        fagsystemId = fagsystemId,
        endringskode = endringskode,
        overføringstidspunkt = overføringstidspunkt,
        avstemmingsnøkkel = avstemmingsnøkkel,
        status = status,
        tidsstempel = tidsstempel
    )

    private fun kopierLikeLinjer(tidligere: Oppdrag) {
        tilstand = if (tidligere.sistedato > this.sistedato) Slett() else Identisk()
        sisteLinjeITidligereOppdrag = tidligere.last()
        this.zip(tidligere).forEach { (a, b) -> tilstand.håndterForskjell(a, b) }
    }

    private fun håndterLengreNåværende(tidligere: Oppdrag) {
        if (this.size <= tidligere.size) return
        this[tidligere.size].kobleTil(linkTo)
        this
            .subList(tidligere.size, this.size)
            .zipWithNext { a, b -> b.kobleTil(a) }
    }

    private fun kobleTil(tidligere: Oppdrag) {
        this.fagsystemId = tidligere.fagsystemId
        this.forEach { it.refFagsystemId = tidligere.fagsystemId }
        this.endringskode = Endringskode.ENDR
    }

    private fun håndterUlikhet(nåværende: Utbetalingslinje, tidligere: Utbetalingslinje) {
        when {
            nåværende.kanEndreEksisterendeLinje(
                tidligere,
                sisteLinjeITidligereOppdrag
            ) -> nåværende.endreEksisterendeLinje(tidligere)
            nåværende.skalOpphøreOgErstatte(tidligere, sisteLinjeITidligereOppdrag) -> opphørTidligereLinjeOgOpprettNy(
                nåværende,
                tidligere
            )
            else -> opprettNyLinje(nåværende)
        }
    }

    private fun opprettNyLinje(nåværende: Utbetalingslinje) {
        nåværende.kobleTil(linkTo)
        linkTo = nåværende
        tilstand = Ny()
    }

    internal fun toHendelseMap() = mapOf(
        "mottaker" to mottaker,
        "fagområde" to "$fagområde",
        "linjer" to map(Utbetalingslinje::toHendelseMap),
        "fagsystemId" to fagsystemId,
        "endringskode" to "$endringskode",
        "tidsstempel" to tidsstempel,
        "nettoBeløp" to nettoBeløp,
        "stønadsdager" to stønadsdager(),
        "avstemmingsnøkkel" to avstemmingsnøkkel?.let { "$it" },
        "status" to status?.let { "$it" },
        "overføringstidspunkt" to overføringstidspunkt,
        "fom" to førstedato,
        "tom" to sistedato
    )

    private interface Tilstand {
        fun håndterForskjell(nåværende: Utbetalingslinje, tidligere: Utbetalingslinje)
    }

    private inner class Identisk : Tilstand {
        override fun håndterForskjell(nåværende: Utbetalingslinje, tidligere: Utbetalingslinje) {
            if (nåværende == tidligere) return nåværende.markerUendret(tidligere)
            håndterUlikhet(nåværende, tidligere)
        }
    }

    private inner class Slett : Tilstand {
        override fun håndterForskjell(nåværende: Utbetalingslinje, tidligere: Utbetalingslinje) {
            if (nåværende == tidligere) {
                if (nåværende == last()) return nåværende.kobleTil(linkTo)
                return nåværende.markerUendret(tidligere)
            }
            håndterUlikhet(nåværende, tidligere)
        }
    }

    private inner class Ny : Tilstand {
        override fun håndterForskjell(nåværende: Utbetalingslinje, tidligere: Utbetalingslinje) {
            nåværende.kobleTil(linkTo)
            linkTo = nåværende
        }
    }

    internal fun toDto() = DtoOppdrag(
        mottaker = mottaker,
        fagområde = fagområde.name,
        linjer = linjer.toDto(),
        fagsystemId = fagsystemId,
        endringskode = endringskode.name,
        nettoBeløp = nettoBeløp,
        overføringstidspunkt = overføringstidspunkt,
        avstemmingsnøkkel = avstemmingsnøkkel,
        status = status?.name,
        tidsstempel = tidsstempel,
    )
}
