package io.pacworx.ambrosia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AmbrosiaApplication

fun main(args: Array<String>) {
    runApplication<AmbrosiaApplication>(*args)
}
