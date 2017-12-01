package wf.garnier.velibtest

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("velib")
data class VelibConfiguration(var stationListUrl: String = "test", var stationDetailUrl: String = "", var pages: Int = 51)
// FIXME: nest configurations ?