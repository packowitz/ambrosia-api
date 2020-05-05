package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/jewelry")
class AdminJewelryController(private val jewelryRepository: JewelryRepository) {

    @PostMapping("open/{type}/{amount}")
    @Transactional
    fun openJewel(@ModelAttribute("player") player: Player, @PathVariable type: JewelType, @PathVariable amount: Int): PlayerActionResponse {
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, type) ?: Jewelry(playerId = player.id, type = type)
        jewelry.lvl1 += amount
        jewelryRepository.save(jewelry)
        return PlayerActionResponse(jewelries = listOf(jewelry))
    }

}
