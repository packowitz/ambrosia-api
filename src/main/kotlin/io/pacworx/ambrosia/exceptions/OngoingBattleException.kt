package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.Player
import org.springframework.http.HttpStatus

class OngoingBattleException(val player: Player) : AmbrosiaException, RuntimeException("There is an ongoing battle. Cannot start a new one.") {
    override val title: String = "Ongoing battle found"
    override val message: String = "Please finish your ongoing battle before starting a new one."
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
