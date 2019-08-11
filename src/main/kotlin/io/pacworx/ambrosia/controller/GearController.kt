package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Gear
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.GearRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("gear")
class GearController(private val gearRepository: GearRepository) {

    @GetMapping
    fun getAllGear(@ModelAttribute("player") player: Player): List<Gear> {
        return gearRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
    }
}