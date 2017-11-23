package wf.garnier.velibtest

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("velib")
data class VelibConfiguration(var stationListUrl: String = "test")
// FIXME: nest configurations ?