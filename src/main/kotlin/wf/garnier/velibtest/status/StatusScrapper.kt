package wf.garnier.velibtest.status

import org.springframework.web.client.AsyncRestTemplate
import wf.garnier.velibtest.VelibConfiguration

class StatusScrapper(val configuration: VelibConfiguration, val client: AsyncRestTemplate) {

    fun getVelibStatus(id: Long) =
            client.getForEntity("${configuration.stationDetailUrl}/$id", StatusResponse::class.java)
}

