package wf.garnier.velibtest.status

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.launch
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import wf.garnier.velibtest.VelibConfiguration
import wf.garnier.velibtest.extensions.toCompletable
import wf.garnier.velibtest.station.Station
import wf.garnier.velibtest.station.StationRepository

@Component
class AsyncScrapingScheduler(
        val state: WebsocketState,
        val repo: StationRepository,
        val scraper: StatusScraper,
        val config: VelibConfiguration
) : IScrapingScheduler {
    val logger = LoggerFactory.getLogger(AsyncScrapingScheduler::class.java)
    val scrapingQueue = Channel<Station>(10)

    override fun startPolling() {
        launch {
            scrape()
            while (true) {
                scheduleScrapes()
            }
        }
    }

    suspend fun scheduleScrapes() {

        repo.findAll()
                .take(config.maxNumberOfStationsToPoll)
                .forEach {
                    if(state.asyncSessions.size > 0){
                        scrapingQueue.send(it)
                        delay(config.sleepDurationBetweenApiCallsMilliseconds)
                    }
                    else {
                        delay(1000L)
                    }
                }
    }

    fun scrape() {
        repeat(20) {
            launch {
                for (station in scrapingQueue) {
                    scrapeAndEmit(station)
                }
            }
        }
    }

    suspend fun scrapeAndEmit(station: Station) {
        try {
            val status = scraper.getVelibStatus(station.id).toCompletable().await()
            logger.debug("Got info for station with id : ${station.id}, emitting.")
            emitMessage(StationStatus(station, status).toJson())
        } catch (e: Exception) {
            logger.error("Error polling stations with id : ${station.id}. Error : ${e.message}.")
        }
    }

    fun emitMessage(message: String) {
        state.asyncSessions.forEach {
            it.sendMessage(TextMessage(message))
        }
    }
}