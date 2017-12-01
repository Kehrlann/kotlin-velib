package wf.garnier.velibtest.status

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import java.time.LocalDateTime

@Component
class ScrappingScheduler(val state: WebsocketState) {

    @Scheduled(fixedRate = 1000L)
    fun emit() {
        state.currentSessions.forEachIndexed { index, session ->
            session.sendMessage(TextMessage("Hi from session #$index, date is ${LocalDateTime.now()}"))
        }
    }
}