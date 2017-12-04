package wf.garnier.velibtest.status

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wf.garnier.velibtest.station.Station

class StationStatusTest {
    val station = Station(1L, "name")

    @Test
    fun `it builds from Station and StatusResponse`() {
        val status = StatusResponse(15, 5, 20, true, true, 1507634830, true)

        val stationStatus = StationStatus(station, status)

        assertThat(stationStatus).isEqualTo(StationStatus(1L, "name", 15, 5, false))
    }

    @Test
    fun `it detects problems`() {
        val statuses = listOf(
                StatusResponse(15, 5, 20, true, true, 1507634830, true),
                StatusResponse(15, 5, 20, true, false, 1507634830, true),
                StatusResponse(15, 5, 20, true, true, 1507634830, false),
                StatusResponse(15, 5, 20, true, false, 1507634830, false)
        )

        val stationStatuses = statuses.map { StationStatus(station, it).hasProblem }

        assertThat(stationStatuses).containsExactly(false, true, true, true)
    }
}