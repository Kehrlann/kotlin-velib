package wf.garnier.velibtest

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.AsyncRestTemplate
import wf.garnier.velibtest.station.StationRepository
import wf.garnier.velibtest.station.StationScraper
import wf.garnier.velibtest.status.IScrapingScheduler
import wf.garnier.velibtest.status.ScrapingScheduler

@Component
@Profile("!test")
class VelibInit(
        val stationRepository: StationRepository,
        val statusscraper: IScrapingScheduler,
        val velibConfiguration: VelibConfiguration
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val scraper = StationScraper(AsyncRestTemplate(), velibConfiguration)
        scraper.getAllStations().forEach { stationRepository.save(it) }

        statusscraper.startPolling()
    }
}