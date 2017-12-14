package wf.garnier.velibtest.status

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class StatusController {

    @GetMapping("status")
    fun statusPage() = "status"

    @GetMapping("metrics")
    fun metricsPage() = "metrics"
}