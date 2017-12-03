package wf.garnier.velibtest.status

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import wf.garnier.velibtest.station.Station
import wf.garnier.velibtest.station.StationRepository
import wf.garnier.velibtest.whenever


class ScrappingSchedulerTest {

    val mockRepo = mock(StationRepository::class.java)
    val mockState = mock(WebsocketState::class.java)
    val mockSession = mock(WebSocketSession::class.java)
    val mockScrapper = mock(StatusScrapper::class.java)
    val stations = listOf(Station(1), Station(2))
    val status = StatusResponse(5, 10, 15)

    @Test
    fun `emit sends a message on each session`() {
        val captors = setupReponsesAndCaptors()
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper)

        scrappingScheduler.emitMessage("TEST")

        val actualMessages = captors.websocketCaptor.allValues.map { it.payload }
        assertThat(actualMessages).containsExactly("TEST")
    }

    @Test
    fun `emit works when there are not sessions`() {
        setupReponsesAndCaptors()
        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper)

        scrappingScheduler.emitMessage("TEST")
    }

    @Test
    fun `pollAndEmit should poll all available stations`() {
        val captors = setupReponsesAndCaptors()

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper)

        scrappingScheduler.pollAndEmit(false)

        assertThat(captors.scrapperCaptor.allValues).isEqualTo(stations.map { it.id })
    }

    @Test
    fun `pollAndEmit should send message through websocket`() {
        val captors = setupReponsesAndCaptors()

        val scrappingScheduler = ScrappingScheduler(mockState, mockRepo, mockScrapper)

        scrappingScheduler.pollAndEmit(false)

        val actualMessages = captors.websocketCaptor.allValues.map { it.payload }
        assertThat(actualMessages).isEqualTo(listOf(status, status).map { it.toString() })
    }


    private fun setupReponsesAndCaptors(): TestCaptors {
        // Database
        whenever(mockRepo.findAll()).thenReturn(stations)

        // Websocket sessions
        whenever(mockState.currentSessions).thenReturn(mutableListOf(mockSession))
        val websocketCaptor= ArgumentCaptor.forClass(WebSocketMessage::class.java)
        whenever(mockSession.sendMessage(websocketCaptor.capture())).thenAnswer {  }

        // Scrapper
        val scrapperResponse = AsyncResult(ResponseEntity(status, HttpStatus.OK))
        val scrapperCaptor = ArgumentCaptor.forClass(Long::class.java)
        whenever(mockScrapper.getVelibStatus(scrapperCaptor.capture())).thenReturn(scrapperResponse)

        return TestCaptors(scrapperCaptor, websocketCaptor)
    }

    private class TestCaptors(val scrapperCaptor: ArgumentCaptor<Long>, val websocketCaptor: ArgumentCaptor<WebSocketMessage<*>>)

}