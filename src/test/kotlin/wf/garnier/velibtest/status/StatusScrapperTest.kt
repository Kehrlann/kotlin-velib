package wf.garnier.velibtest.status

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.xml.bind.JAXBContext


class StatusScrapperTest {

    private val velibDetail = javaClass.classLoader.getResourceAsStream("fixtures/velib-status-detail.xml")

    @Test
    fun `properly deserialize response`() {
        val jc = JAXBContext.newInstance(StatusResponse::class.java)
        val unmarshaller = jc.createUnmarshaller()

        val result = unmarshaller.unmarshal(velibDetail) as StatusResponse

        assertThat(result).isEqualTo(
                StatusResponse(
                        available = 5,
                        free = 20,
                        total = 25,
                        ticket = 1,
                        open = false,
                        updated = 1507634830,
                        connected = false
                )
        )
    }
}