package wf.garnier.velibtest.status

import org.springframework.stereotype.Component
import org.springframework.web.client.AsyncRestTemplate
import wf.garnier.velibtest.VelibConfiguration

@Component
class StatusScraper(val configuration: VelibConfiguration) {

    val client: AsyncRestTemplate = AsyncRestTemplate()

    fun getVelibStatus(id: Long) =
            client.getForEntity("${configuration.stationDetailUrl}/$id", StatusResponse::class.java)
}

