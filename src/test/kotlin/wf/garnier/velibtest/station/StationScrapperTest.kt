package wf.garnier.velibtest.station

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.web.client.RestTemplate
import wf.garnier.velibtest.VelibConfiguration


class StationScrapperTest {
    private val onePageOfStations =
            javaClass.classLoader.getResourceAsStream("fixtures/velib-station-list.html").reader().readText()
    val mockClient = mock(RestTemplate::class.java)

    @Test
    fun `it should get a given page`() {
        val stationScrapper = StationScrapper(mockClient, VelibConfiguration())
        val httpBody = "myPageReturn"

        Mockito.`when`(mockClient.getForObject<String>(Matchers.anyString(), any()))
                .thenReturn(httpBody)

        val page = stationScrapper.getPage(12)

        assertThat(page).isEqualTo(httpBody)
    }

    @Test
    fun `it should touch the correct page endpoint`() {
        val endpoint = "test-url"
        val stationScrapper = StationScrapper(mockClient, VelibConfiguration(stationListUrl = endpoint))
        Mockito.`when`(mockClient.getForObject<String>(Matchers.anyString(), any()))
                .thenReturn("")

        stationScrapper.getPage(12)

        verify(mockClient).getForObject(eq("test-url/p-12/to-2.html"), eq(String::class.java))
    }

    @Test
    fun `it should scrape all stations from a page`() {
        val stationScrapper = StationScrapper(mockClient, VelibConfiguration())

        val stations = stationScrapper.parseStationsFromPage(onePageOfStations)

        assertThat(stations)
                .contains(Station(1020, "RIVOLI CONCORDE"), Station(1002, "PLACE DU CHATELET"))
    }

    @Test
    fun `it should get all stations`() {
        val stationScrapper = StationScrapper(mockClient, VelibConfiguration())
        Mockito.`when`(mockClient.getForObject<String>(Matchers.anyString(), any()))
                .thenReturn(onePageOfStations)

        val stations = stationScrapper.getAllStations()

        assertThat(stations.size).isEqualTo(1275)
    }
}