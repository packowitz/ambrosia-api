package io.pacworx.ambrosia.exceptions

import org.springframework.http.HttpStatus

class UnauthenticatedException(override val message: String = "Unknown player or player is currently locked") : AmbrosiaException, RuntimeException(message) {
    override val title: String = "Not authenticated"
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED
}
