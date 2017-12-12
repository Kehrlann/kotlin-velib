package wf.garnier.velibtest.status

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import wf.garnier.velibtest.VelibConfiguration
import wf.garnier.velibtest.station.StationRepository

@Component
@Profile("sync")
class SyncScrapingScheduler(
        val state: WebsocketState,
        val repo: StationRepository,
        val scraper: StatusScraper,
        val config: VelibConfiguration
): IScrapingScheduler {
    val logger = LoggerFactory.getLogger(ScrapingScheduler::class.java)

    override fun startPolling() {
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