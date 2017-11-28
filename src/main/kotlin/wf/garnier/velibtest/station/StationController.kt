package wf.garnier.velibtest.station

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class StationController(val stationRepository: StationRepository) {

    @GetMapping("stations")
    fun listStations(model: Model): String {
        model.addAttribute("stations", stationRepository.findAll().sortedBy { it.id })
        return "stations"
    }
}