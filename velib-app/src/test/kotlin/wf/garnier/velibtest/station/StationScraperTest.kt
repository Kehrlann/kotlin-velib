package wf.garnier.velibtest.station

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.web.client.AsyncRestTemplate
import wf.garnier.velibtest.VelibConfiguration
import wf.garnier.velibtest.whenever


class StationScraperTest {
    private val onePageOfStations =
            javaClass.classLoader.getResourceAsStream("fixtures/velib-station-list.html").reader().readText()
    val mockClient = mock(AsyncRestTemplate::class.java)

    @Test
    fun `it should get a given page`() {
        val stationscraper = StationScraper(mockClient, VelibConfiguration())
        val httpBody = "myPageReturn"

        setupResponse(httpBody)

        val page = stationscraper.getPage(12).blockingLast().body

        assertThat(page).isEqualTo(httpBody)
    }

    @Test
    fun `it should touch the correct page endpoint`() {
        val endpoint = "test-url"
        val stationscraper = StationScraper(mockClient, VelibConfiguration(stationListUrl = endpoint))
        setupResponse("")

        stationscraper.getPage(12)

        verify(mockClient).getForEntity(eq("test-url/p-12/to-2.html"), eq(String::class.java))
    }

    @Test
    fun `it should get all pages`() {
        val pages = 12
        val stationscraper = StationScraper(mockClient, VelibConfiguration(pages = pages))
        setupResponse("")

        stationscraper.getAllStations()

        (1..12).forEach {
            verify(mockClient).getForEntity(contains("/p-$it/"), eq(String::class.java))
        }
    }

    @Test
    fun `it should scrape all stations from a page`() {
        val stationscraper = StationScraper(mockClient, VelibConfiguration())

        val stations = stationscraper.parseStationsFromPage(onePageOfStations)

        assertThat(stations)
                .contains(Station(1020, "RIVOLI CONCORDE"), Station(1002, "PLACE DU CHATELET"))
    }

    @Test
    fun `it should get all stations`() {
        val stationscraper = StationScraper(mockClient, VelibConfiguration())
        setupResponse(onePageOfStations)

        val stations = stationscraper.getAllStations()

        assertThat(stations.size).isEqualTo(1275)
    }

    private fun setupResponse(response: String) {
        whenever(mockClient.getForEntity<String>(anyString(), any()))
                .thenReturn(AsyncResult.forValue(ResponseEntity(response, HttpStatus.OK)))
    }
}
