package wf.garnier.velibtest.status

import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import wf.garnier.velibtest.VelibConfiguration
import wf.garnier.velibtest.station.StationRepository

@Component
class SyncScrapingScheduler(
        val state: WebsocketState,
        val repo: StationRepository,
        val scraper: StatusScraper,
        val config: VelibConfiguration
) : IScrapingScheduler {
    val logger = LoggerFactory.getLogger(AsyncScrapingScheduler::class.java)

    override fun startPolling() {
        launch {
            while (true) {
                getStatusAndEmitAllStations()
            }
        }
    }

    fun emitMessage(message: String) {
        state.syncSessions.forEach {
            it.sendMessage(TextMessage(message))
        }
    }

    fun getStatusAndEmitAllStations() {
        val stations = repo.findAll()

        stations.forEach {
            if(state.syncSessions.size == 0) {
                return@forEach  // equivalent to java "continue"
            }

            try {
                val status = scraper.getVelibStatus(it.id).get().body
                logger.debug("Got info for station with id : ${it.id}, emitting.")
                emitMessage(StationStatus(it, status).toJson())
            } catch (e: Exception) {
                logger.error("Error polling stations with id : ${it.id}. Error : ${e.message}.")
            }
            Thread.sleep(config.sleepDurationBetweenApiCallsMilliseconds)
        }
    }
}