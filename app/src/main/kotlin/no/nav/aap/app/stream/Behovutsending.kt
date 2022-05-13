package no.nav.aap.app.stream

import no.nav.aap.app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.app.kafka.Topics
import no.nav.aap.domene.utbetaling.observer.MottakerObserver
import no.nav.aap.kafka.streams.Behov
import no.nav.aap.kafka.streams.BehovVisitor
import no.nav.aap.kafka.streams.branch
import no.nav.aap.kafka.streams.sendBehov
import org.apache.kafka.streams.kstream.KStream

internal fun KStream<String, BehovUtbetaling>.sendBehov(name: String): Unit = sendBehov(name) {
    branch(Topics.utbetalingsbehov, "$name-barn", BehovUtbetaling::erUtbetalingsbehov, ::UtbetalingsbehovVisitor)
}

internal interface BehovUtbetalingVisitor : BehovVisitor<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov> {
    fun utbetalingsbehov(kafkaUtbetalingsbehov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov) {}
}

internal interface BehovUtbetaling : Behov<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov, BehovUtbetalingVisitor> {
    fun erUtbetalingsbehov() = false
}

private class UtbetalingsbehovVisitor : BehovUtbetalingVisitor {
    private lateinit var utbetalingsbehov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov

    override fun utbetalingsbehov(kafkaUtbetalingsbehov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov) {
        this.utbetalingsbehov = kafkaUtbetalingsbehov
    }

    override fun toJson() = utbetalingsbehov
}

internal class BehovObserver(private val ident: String) : MottakerObserver {
    private val behovUtbetaling = mutableListOf<BehovUtbetaling>()

    fun behovene() = behovUtbetaling.toList()

    override fun behovBarn() {
        behovUtbetaling.add(
            KafkaUtbetalingsbehovWrapper(
                KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov(
                    KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Request(ident),
                    null
                )
            )
        )
    }
}
