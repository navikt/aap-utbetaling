package no.nav.aap.dto.kafka

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MottakereKafkaDtoTest {

    // TODO: lag en test som f.eks sjekker at versjonsnummer er bumpa ved endring

    @Test
    fun `test versjonsnummer`() {
        assertEquals(12, MottakereKafkaDto.VERSION)
    }
}
