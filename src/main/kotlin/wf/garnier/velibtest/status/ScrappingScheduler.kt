package wf.garnier.velibtest.status

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import wf.garnier.velibtest.VelibConfiguration
import wf.garnier.velibtest.station.StationRepository

@Component
class ScrappingScheduler(
        val state: WebsocketState,
        val repo: StationRepository,
        val scrapper: StatusScrapper,
        val config: VelibConfiguration
) {
    val logger = LoggerFactory.getLogger(ScrappingScheduler::class.java)

    fun startPolling() {
        while (true) {
            getStatusAndEmitAllStations()
        }
    }

    fun emitMessage(message: String) {
        state.currentSessions.forEach {
            it.sendMessage(TextMessage(message))
        }
    }

    fun getStatusAndEmitAllStations() {
        val stations = repo.findAll()

        stations.forEach {
            try {
                val status = scrapper.getVelibStatus(it.id).get().body
                logger.debug("Got info for station with id : ${it.id}, emitting.")
                emitMessage(StationStatus(it, status).toJson())
            } catch (e: Exception) {
                logger.error("Error polling stations with id : ${it.id}. Error : ${e.message}.")
            }
            Thread.sleep(config.sleepDurationBetweenApiCallsMilliseconds)
        }
    }
}