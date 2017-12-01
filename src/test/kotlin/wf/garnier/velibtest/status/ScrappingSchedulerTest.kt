package wf.garnier.velibtest.status

import org.junit.Test
import org.mockito.Mockito.*
import org.springframework.web.socket.WebSocketSession


class ScrappingSchedulerTest {

    @Test
    fun `emit sends a message on each session`() {
        val mockSession = mock(WebSocketSession::class.java)

        val mockState = mock(WebsocketState::class.java)
        `when`(mockState.currentSessions).thenReturn(mutableListOf(mockSession, mockSession))

        val scrappingScheduler = ScrappingScheduler(mockState)

        scrappingScheduler.emit()

        verify(mockSession, times(2)).sendMessage(any())
    }

    @Test
    fun `emit works when there are not sessions`() {
        val mockState = mock(WebsocketState::class.java)
        `when`(mockState.currentSessions).thenReturn(mutableListOf())

        val scrappingScheduler = ScrappingScheduler(mockState)

        scrappingScheduler.emit()
    }
}