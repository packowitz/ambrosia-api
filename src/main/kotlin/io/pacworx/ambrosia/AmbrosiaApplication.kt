package io.pacworx.ambrosia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AmbrosiaApplication

fun main(args: Array<String>) {
    runApplication<AmbrosiaApplication>(*args)
}
