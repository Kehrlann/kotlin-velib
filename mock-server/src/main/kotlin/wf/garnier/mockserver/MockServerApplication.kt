package wf.garnier.mockserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MockServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(MockServerApplication::class.java, *args)
}
