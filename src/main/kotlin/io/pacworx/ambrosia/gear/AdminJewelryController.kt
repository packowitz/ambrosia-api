package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Jewelry
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.JewelryRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/jewelry")
class AdminJewelryController(val jewelryRepository: JewelryRepository) {

    @PostMapping("open/{type}/{amount}")
    fun openJewel(@ModelAttribute("player") player: Player, @PathVariable type: JewelType, @PathVariable amount: Int): PlayerActionResponse {
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, type) ?: Jewelry(playerId = player.id, type = type)
        jewelry.lvl1 += amount
        jewelryRepository.save(jewelry)
        return PlayerActionResponse(jewelries = listOf(jewelry))
    }

}
