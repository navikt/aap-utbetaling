package no.nav.aap.app

import no.nav.aap.kafka.streams.KStreamsConfig

data class Config(
    val kafka: KStreamsConfig
)
