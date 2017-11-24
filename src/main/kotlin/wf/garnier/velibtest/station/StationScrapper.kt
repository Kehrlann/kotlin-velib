package wf.garnier.velibtest.station

import org.jsoup.Jsoup
import org.springframework.web.client.RestTemplate
import wf.garnier.velibtest.VelibConfiguration

class StationScrapper(
        val client: RestTemplate,
        val config: VelibConfiguration
) {

    internal fun parseStationsFromPage(page: String): Collection<Station> {
        val soup = Jsoup.parse(page)
        return soup.select("table.table1 > tbody > tr").map {
            val id = it.select("td:nth-child(1)").text().toLong()
            val nom = it.select("td > a").text()
            Station(id, nom)
        }
    }

    internal fun getPage(i: Int) = client.getForObject(config.stationListUrl + "/p-$i/to-2.html", String::class.java)!!

    fun getAllStations() = (1..51).map {
        getPage(it)
    }.flatMap {
        parseStationsFromPage(it)
    }

}