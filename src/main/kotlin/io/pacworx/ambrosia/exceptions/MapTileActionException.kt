package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.Player
import org.springframework.http.HttpStatus

class MapTileActionException(val player: Player, val action: String, val mapId: Long, val posX: Int, val posY: Int) : AmbrosiaException, RuntimeException("Cannot perform action on map tile") {
    override val title: String = "Cannot $action map tile"
    override val message: String = "Map tile ${posX}x${posY} is either unknown to you or $action that tile is not allowed."
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
