package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.Player
import org.springframework.http.HttpStatus

class UnauthorizedException(val player: Player, override val message: String) : AmbrosiaException, RuntimeException(message) {
    override val title: String = "Not allowed"
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED
}
