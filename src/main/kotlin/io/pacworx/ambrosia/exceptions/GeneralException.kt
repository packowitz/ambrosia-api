package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.Player
import org.springframework.http.HttpStatus

class GeneralException(val player: Player, override val title: String, override val message: String) : AmbrosiaException, RuntimeException(message) {
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
