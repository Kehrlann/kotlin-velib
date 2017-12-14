package wf.garnier.mockserver

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.xml.bind.annotation.XmlRootElement

@RestController
class Controller {

    @GetMapping("/{id}", produces = arrayOf("application/xml"))
    fun getStation(@PathVariable("id") id: Int): StatusResponse {
        val total = totalBikes(id)
        val available = availableBikes(total)
        val free = total - available
        sleepRandom()
        return StatusResponse(available, free)
    }

    private fun totalBikes(id: Int) = ((id % 7) * 5) + 20 + id % 3 // seven buckets between 20 and 50, with a +1/2

    private fun availableBikes(max: Int) = (0..max).random()

    private fun sleepRandom() = Thread.sleep((0..500).random().toLong())
}

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) +  start


@XmlRootElement(name = "station")
data class StatusResponse(
        var available: Int = 0,
        var free: Int = 0,
        var total: Int = available + free,
        var ticket: Boolean = false,
        var open: Boolean = true,
        var connected: Boolean = true,
        var updated: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)