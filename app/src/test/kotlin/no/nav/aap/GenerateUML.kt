package no.nav.aap

import no.nav.aap.app.createTopology
import no.nav.aap.kafka.streams.uml.KStreamsUML
import org.apache.kafka.streams.StreamsBuilder
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

internal class GenerateUML {
    private companion object{
        private val log = LoggerFactory.getLogger("GenerateUML")
    }

    @Test
    fun `generate topology UML`() {
        val topology = StreamsBuilder().apply { createTopology() }.build()

        KStreamsUML.create(topology).also {
            log.info("Generated topology UML ${it.absoluteFile}. Online editor: https://plantuml-editor.kkeisuke.dev")
        }
    }
}
