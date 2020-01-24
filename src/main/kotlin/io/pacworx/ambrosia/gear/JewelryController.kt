package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("jewelry")
class JewelryController(private val jewelryRepository: JewelryRepository) {

    @GetMapping("")
    fun getGears(@ModelAttribute("player") player: Player): List<Jewelry> {
        return jewelryRepository.findAllByPlayerId(player.id)
    }

    @PostMapping("merge/{type}/{lvl}")
    @Transactional
    fun mergeJewels(@ModelAttribute("player") player: Player, @PathVariable type: JewelType, @PathVariable lvl: Int): PlayerActionResponse {
        if (lvl == 10) {
            throw RuntimeException("You cannot merge jewels of level 10 as it is already highest level")
        }
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, type)
        if (jewelry == null || jewelry.getAmount(lvl) < 4) {
            throw RuntimeException("You don't have enough jewels of $type $lvl* to merge them into a higher level")
        }
        jewelry.increaseAmount(lvl, -4)
        jewelry.increaseAmount((lvl + 1), 1)
        return PlayerActionResponse(jewelries = listOf(jewelry))
    }
}
