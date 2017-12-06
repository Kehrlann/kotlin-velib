package wf.garnier.velibtest.status

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.mock
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
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


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
    fun `pollAndEmit should poll all available stations`() {
        val captors = setupReponsesAndCaptors()

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.getStatusAndEmitAllStations()

        assertThat(captors.scrapperCaptor.allValues).isEqualTo(stations.map { it.id })
    }

    @Test
    fun `pollAndEmit should send message through websocket`() {
        val captors = setupReponsesAndCaptors()

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.getStatusAndEmitAllStations()

        val actualMessages = captors.websocketCaptor.allValues.map { it.payload }
        assertThat(actualMessages).isEqualTo(statuses.map { it.toJson() })
    }

    @Test
    fun `pollAndEmit should not fail when HTTP fails`() {
        val httpSuccess = AsyncResult(ResponseEntity(status, HttpStatus.OK))

        // Return the first station twice
        val captors = setupReponsesAndCaptors(listOf(stations.first(), stations.first()))

        whenever(mockScrapper.getVelibStatus(anyLong()))
                .thenThrow(HttpClientErrorException(HttpStatus.I_AM_A_TEAPOT))
                .thenReturn(httpSuccess)

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, config)

        scrappingScheduler.getStatusAndEmitAllStations()

        val actualMessages = captors.websocketCaptor.allValues.map { it.payload }
        assertThat(actualMessages).isEqualTo(statuses.take(1).map { it.toJson() })
    }

    @Test
    fun `pollAndEmit sleeps during between calls`() {
        // Arrange
        val stationsToPoll = listOf(Station(), Station(), Station())
        setupReponsesAndCaptors(stationsToPoll)
        val millisBetweenPolls = 500L

        val startTime = LocalDateTime.now()
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper, VelibConfiguration(sleepDurationBetweenApiCallsMilliseconds = millisBetweenPolls))

        // Act
        scrappingScheduler.getStatusAndEmitAllStations()

        // Assert
        val timeSpan = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS)
        assertThat(timeSpan).isGreaterThan(stationsToPoll.size * millisBetweenPolls) // LONGER than the min duration
        assertThat(timeSpan).isLessThan((stationsToPoll.size + 1) * millisBetweenPolls) // But not too long.
        // NB : the above is a bit flaky, it the total cost of ops of polls is greater than the "millisBetweenPolls",
        // the test might fail. A cleaner way to test this could be to inject a "clock" object that then could be mocked.
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