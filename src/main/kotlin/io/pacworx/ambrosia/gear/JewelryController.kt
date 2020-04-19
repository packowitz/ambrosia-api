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
    fun getJewelries(@ModelAttribute("player") player: Player): List<Jewelry> {
        return jewelryRepository.findAllByPlayerId(player.id)
    }
}
