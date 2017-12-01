package wf.garnier.velibtest.status

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.xml.bind.annotation.XmlRootElement


@XmlRootElement(name = "station")
data class StatusResponse(
        var available: Int = 0,
        var free: Int = 0,
        var total: Int = 0,
        var ticket: Int = 0,
        var open: Boolean = false,
        var updated: Long = 0,
        var connected: Boolean = false
) {
    val lastUpdate
        get() = LocalDateTime.ofInstant(Instant.ofEpochSecond(updated), ZoneId.of("UTC"))
}