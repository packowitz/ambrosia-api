package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.PlayerRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("player")
class PlayerController(private val playerRepository: PlayerRepository) {

    @GetMapping("")
    fun getPlayer(@ModelAttribute("player") player: Player): Player {
        return player
    }
}