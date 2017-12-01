package wf.garnier.velibtest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.socket.config.annotation.EnableWebSocket

@SpringBootApplication
@EnableWebSocket
@EnableScheduling
class VelibTestApplication

fun main(args: Array<String>) {
    SpringApplication.run(VelibTestApplication::class.java, *args)
}
