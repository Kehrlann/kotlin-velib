package wf.garnier.velibtest

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import wf.garnier.velibtest.status.AsyncWebsocketHandler
import wf.garnier.velibtest.status.SyncWebsocketHandler
import wf.garnier.velibtest.status.WebsocketState

@Configuration
class WebsocketConfiguration(val state: WebsocketState): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(AsyncWebsocketHandler(state), "/ws/status/async")
        registry.addHandler(SyncWebsocketHandler(state), "/ws/status/sync")
    }
}
