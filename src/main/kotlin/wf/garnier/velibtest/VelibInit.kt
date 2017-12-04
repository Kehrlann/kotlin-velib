package wf.garnier.velibtest

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.client.AsyncRestTemplate
import wf.garnier.velibtest.station.StationRepository
import wf.garnier.velibtest.station.StationScrapper
import wf.garnier.velibtest.status.ScrappingScheduler

@Component
@Profile("!test")
class VelibInit(
        val stationRepository: StationRepository,
        val statusScrapper: ScrappingScheduler,
        val velibConfiguration: VelibConfiguration
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val scrapper = StationScrapper(AsyncRestTemplate(), velibConfiguration)
        scrapper.getAllStations().forEach { stationRepository.save(it) }

        statusScrapper.startPolling()
    }
}