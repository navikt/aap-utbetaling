package no.nav.aap

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import no.nav.aap.app.topology
import no.nav.aap.kafka.streams.topology.Mermaid
import org.apache.kafka.clients.producer.MockProducer
import org.junit.jupiter.api.Test
import java.io.File

internal class TopologyDiagram {

    @Test
    fun `generate topology mermaid diagram`() {
        val topology = topology(SimpleMeterRegistry(), MockProducer())

        val graph = Mermaid.graph("Utbetaling", topology)
        File("../doc/topology.mermaid").apply { writeText(graph) }

        val mermaidGraphMarkdown = markdown(graph)
        File("../doc/topology.md").apply { writeText(mermaidGraphMarkdown) }
    }
}

fun markdown(mermaid: String) = """
```mermaid
$mermaid
```
"""
