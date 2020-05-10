package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/jewelry")
class AdminJewelryController(private val jewelryRepository: JewelryRepository) {

    @PostMapping("open/{type}/{level}/{amount}")
    @Transactional
    fun openJewel(@ModelAttribute("player") player: Player,
                  @PathVariable type: JewelType,
                  @PathVariable level: Int,
                  @PathVariable amount: Int): PlayerActionResponse {
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, type) ?: Jewelry(playerId = player.id, type = type)
        jewelry.increaseAmount(level, amount)
        jewelryRepository.save(jewelry)
        return PlayerActionResponse(jewelries = listOf(jewelry))
    }

}
