package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.Player
import org.springframework.http.HttpStatus

class EntityNotFoundException(val player: Player, val entityName: String, val entityId: Long) : AmbrosiaException, RuntimeException("Requested entity was not found") {
    override val title: String = "Unknown $entityName"
    override val message: String = "Requested $entityName (#$entityId) does not exist. Please refresh your view."
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
}
