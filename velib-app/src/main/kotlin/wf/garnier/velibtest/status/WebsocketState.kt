package wf.garnier.velibtest.status

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Component
class WebsocketState {
    val currentSessions: MutableList<WebSocketSession> = Collections.synchronizedList(mutableListOf())
}