package io.pacworx.ambrosia.exceptions

import org.springframework.http.HttpStatus

class ConfigurationException(override val message: String) : AmbrosiaException, RuntimeException(message) {
    override val title: String = "Wrong configuration"
    override val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
}
