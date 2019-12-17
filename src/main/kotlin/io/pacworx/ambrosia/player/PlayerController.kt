package io.pacworx.ambrosia.player

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PlayerService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("player")
class PlayerController(private val playerService: PlayerService) {

    @PostMapping("")
    fun getPlayer(@ModelAttribute("player") player: Player): PlayerActionResponse {
        return playerService.response(player)
    }
}