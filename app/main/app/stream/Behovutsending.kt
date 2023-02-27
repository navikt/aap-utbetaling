package app.stream

import app.kafka.KafkaUtbetalingsbehovWrapper
import no.nav.aap.domene.utbetaling.modellapi.MottakerModellApiObserver
import no.nav.aap.kafka.streams.v2.behov.Behov
import no.nav.aap.kafka.streams.v2.behov.BehovExtractor

internal interface BehovUtbetalingVisitor {
    fun utbetalingsbehov(kafkaUtbetalingsbehov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov) {}
}

internal interface BehovUtbetaling : Behov<BehovUtbetalingVisitor> {
    fun erUtbetalingsbehov() = false
}

internal class UtbetalingsbehovVisitor : BehovUtbetalingVisitor,
    BehovExtractor<KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov> {
    private lateinit var utbetalingsbehov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov

    override fun utbetalingsbehov(kafkaUtbetalingsbehov: KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov) {
        this.utbetalingsbehov = kafkaUtbetalingsbehov
    }

    override fun toJson() = utbetalingsbehov
}

internal class BehovObserver : MottakerModellApiObserver {
    private val behovUtbetaling = mutableListOf<BehovUtbetaling>()

    fun behovene() = behovUtbetaling.toList()

    override fun behovBarn() {
        behovUtbetaling.add(
            KafkaUtbetalingsbehovWrapper(
                KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov(
                    KafkaUtbetalingsbehovWrapper.KafkaUtbetalingsbehov.Request(),
                    null
                )
            )
        )
    }
}
