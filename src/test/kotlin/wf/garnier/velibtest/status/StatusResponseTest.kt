package wf.garnier.velibtest.status

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDate


class StatusResponseTest {
    @Test
    fun `date conversion`() {
        val underTest = StatusResponse(1, 1, 2, 0, false, 1507634830, false)

        assertThat(underTest.lastUpdate.toLocalDate()).isEqualTo(LocalDate.of(2017, 10 , 10))
    }
}