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


class ScrappingSchedulerTest {

    val mockRepo = mock(StationRepository::class.java)
    val mockState = mock(WebsocketState::class.java)
    val mockSession = mock(WebSocketSession::class.java)
    val mockScrapper = mock(StatusScrapper::class.java)
    val stations = listOf(Station(1, "first"), Station(2, "second"))
    val status = StatusResponse(5, 10, 15)
    val statuses = stations.map { StationStatus(it, status) }
    val config = VelibConfiguration(sleepDurationBetweenApiCallsMilliseconds = 0L)

    @Test
    fun `emit sends a message on each session`() {
        val captors = setupReponsesAndCaptors()
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.emitMessage("TEST")

        val actualMessages = captors.websocketCaptor.allValues.map { it.payload }
        assertThat(actualMessages).containsExactly("TEST")
    }

    @Test
    fun `emit works when there are not sessions`() {
        setupReponsesAndCaptors()
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.emitMessage("TEST")
    }

    @Test
    fun `scrapeStation should poll all available stations`() {
        val captors = setupReponsesAndCaptors()

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.scrapeAndEmit(stations.first())

        assertThat(captors.scrapperCaptor.value).isEqualTo(stations.first().id)
    }

    @Test
    fun `scrapeStation should send message through websocket`() {
        val captors = setupReponsesAndCaptors()

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.scrapeAndEmit(stations.first())

        assertThat(captors.websocketCaptor.value.payload).isEqualTo(statuses.first().toJson())
    }

    @Test
    fun `scrapeStation should not fail when HTTP fails`() {
        whenever(mockScrapper.getVelibStatus(anyLong()))
                .thenThrow(HttpClientErrorException(HttpStatus.I_AM_A_TEAPOT))

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.scrapeAndEmit(stations.first())
    }

    @Test
    fun `scrape relies on scrapeAndEmit`() {
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)
        val spy = spy(scrappingScheduler)

        spy.scrape()

        runBlocking {
            (1L..5L).forEach {
                spy.scrappingQueue.send(Station(it))
            }
        }

        fun anyStatus() = any() ?: Station()
        verify(spy, times(5)).scrapeAndEmit(anyStatus())
    }

    @Test
    fun `scheduleScapes schedules the stations to be scrapped`() {
        setupReponsesAndCaptors()
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)
        val received = mutableListOf<Station>()

        runBlocking {
            scrappingScheduler.scheduleScrapes()
            stations.forEach {
                received.add(scrappingScheduler.scrappingQueue.receive())
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

        // Scrapper
        val scrapperResponse = AsyncResult(ResponseEntity(status, HttpStatus.OK))
        val scrapperCaptor = ArgumentCaptor.forClass(Long::class.java)
        whenever(mockScrapper.getVelibStatus(scrapperCaptor.capture())).thenReturn(scrapperResponse)

        return TestCaptors(scrapperCaptor, websocketCaptor)
    }

    private class TestCaptors(val scrapperCaptor: ArgumentCaptor<Long>, val websocketCaptor: ArgumentCaptor<WebSocketMessage<*>>)

}