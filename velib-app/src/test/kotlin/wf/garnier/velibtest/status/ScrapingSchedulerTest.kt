package wf.garnier.velibtest.status

import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import wf.garnier.velibtest.VelibConfiguration
import wf.garnier.velibtest.station.Station
import wf.garnier.velibtest.station.StationRepository
import wf.garnier.velibtest.whenever


class ScrapingSchedulerTest {

    val mockRepo = mock(StationRepository::class.java)
    val mockState = mock(WebsocketState::class.java)
    val mockSession = mock(WebSocketSession::class.java)
    val mockscraper = mock(StatusScraper::class.java)
    val stations = listOf(Station(1, "first"), Station(2, "second"))
    val status = StatusResponse(5, 10, 15)
    val statuses = stations.map { StationStatus(it, status) }
    val config = VelibConfiguration(sleepDurationBetweenApiCallsMilliseconds = 0L)

    @Test
    fun `emit sends a message on each session`() {
        val captors = setupReponsesAndCaptors()
        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)

        scrapingScheduler.emitMessage("TEST")

        val actualMessages = captors.websocketCaptor.allValues.map { it.payload }
        assertThat(actualMessages).containsExactly("TEST")
    }

    @Test
    fun `emit works when there are not sessions`() {
        setupReponsesAndCaptors()
        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)

        scrapingScheduler.emitMessage("TEST")
    }

    @Test
    fun `scrapeStation should poll all available stations`() {
        val captors = setupReponsesAndCaptors()

        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)

        runBlocking { scrapingScheduler.scrapeAndEmit(stations.first()) }

        assertThat(captors.scraperCaptor.value).isEqualTo(stations.first().id)
    }

    @Test
    fun `scrapeStation should send message through websocket`() {
        val captors = setupReponsesAndCaptors()

        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)

        runBlocking { scrapingScheduler.scrapeAndEmit(stations.first()) }

        assertThat(captors.websocketCaptor.value.payload).isEqualTo(statuses.first().toJson())
    }

    @Test
    fun `scrapeStation should not fail when HTTP fails`() {
        whenever(mockscraper.getVelibStatus(anyLong()))
                .thenThrow(HttpClientErrorException(HttpStatus.I_AM_A_TEAPOT))

        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)

        runBlocking { scrapingScheduler.scrapeAndEmit(stations.first()) }
    }

    @Test
    fun `scrape relies on scrapeAndEmit`() {
        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)
        val spy = spy(scrapingScheduler)

        spy.scrape()

        runBlocking {
            (1L..5L).forEach {
                spy.scrapingQueue.send(Station(it))
            }
        }

        fun anyStatus() = any() ?: Station()
        runBlocking { verify(spy, times(5)).scrapeAndEmit(anyStatus()) }
    }

    @Test
    fun `scheduleScapes schedules the stations to be scraped`() {
        setupReponsesAndCaptors()
        val scrapingScheduler = ScrapingScheduler(mockState, mockRepo, mockscraper, config)
        val received = mutableListOf<Station>()

        runBlocking {
            scrapingScheduler.scheduleScrapes()
            stations.forEach {
                received.add(scrapingScheduler.scrapingQueue.receive())
            }
        }
        assertThat(received).isEqualTo(stations)
    }

    private fun setupReponsesAndCaptors(
            stations: Collection<Station> = this.stations
    ): TestCaptors {
        // Database
        whenever(mockRepo.findAll()).thenReturn(stations)

        // Websocket sessions
        whenever(mockState.currentSessions).thenReturn(mutableListOf(mockSession))
        val websocketCaptor = ArgumentCaptor.forClass(WebSocketMessage::class.java)
        whenever(mockSession.sendMessage(websocketCaptor.capture())).thenAnswer { }

        // scraper
        val scraperResponse = AsyncResult(ResponseEntity(status, HttpStatus.OK))
        val scraperCaptor = ArgumentCaptor.forClass(Long::class.java)
        whenever(mockscraper.getVelibStatus(scraperCaptor.capture())).thenReturn(scraperResponse)

        return TestCaptors(scraperCaptor, websocketCaptor)
    }

    private class TestCaptors(val scraperCaptor: ArgumentCaptor<Long>, val websocketCaptor: ArgumentCaptor<WebSocketMessage<*>>)

}