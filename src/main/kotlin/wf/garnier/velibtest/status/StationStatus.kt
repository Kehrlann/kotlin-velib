package wf.garnier.velibtest.status

import wf.garnier.velibtest.station.Station

data class StationStatus(val id: Long, val name: String, val available: Int, val free: Int, val hasProblem: Boolean) {

    constructor(station: Station, status: StatusResponse):
            this(station.id, station.nom, status.available, status.free, !status.connected || !status.open)
}