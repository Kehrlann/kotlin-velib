package wf.garnier.velibtest.status

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import wf.garnier.velibtest.station.StationRepository

@Component
class ScrappingScheduler(
        val state: WebsocketState,
        val repo: StationRepository,
        val scrapper: StatusScrapper
) {

    fun emitMessage(message: String) {
        state.currentSessions.forEach {
            it.sendMessage(TextMessage(message))
        }
    }

    fun pollAndEmit(continuously: Boolean = true) {
        do {
            val stations = repo.findAll()

            stations.forEach {
                val status = scrapper.getVelibStatus(it.id).get().body
                emitMessage(status.toString())
            }
        } while (continuously)
    }
}