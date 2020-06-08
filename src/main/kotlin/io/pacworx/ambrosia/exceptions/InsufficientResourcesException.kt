package io.pacworx.ambrosia.exceptions

import org.springframework.http.HttpStatus

class InsufficientResourcesException(val playerId: Long, val resource: String, val amount: Int) : AmbrosiaException, RuntimeException("Insufficient $resource") {
    override val title: String = "Insufficient $resource"
    override val message: String = "You don't have $amount $resource"
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
