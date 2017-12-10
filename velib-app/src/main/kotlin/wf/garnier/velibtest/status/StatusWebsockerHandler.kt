package wf.garnier.velibtest.status

import org.slf4j.LoggerFactory
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class StatusWebsockerHandler(val database: WebsocketState) : TextWebSocketHandler() {

    val logger = LoggerFactory.getLogger(StatusWebsockerHandler::class.java)

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        logger.info("RECEIVED MESSAGE : ${message.payload}")
    }

    override fun afterConnectionEstablished(session:WebSocketSession) {
        database.currentSessions.add(session)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        database.currentSessions.remove(session)
    }

}