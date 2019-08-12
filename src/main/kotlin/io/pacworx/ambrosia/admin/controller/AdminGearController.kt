package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.GearService
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.transaction.Transactional
import kotlin.random.Random

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/gear")
class AdminGearController(val gearService: GearService,
                          val gearRepository: GearRepository,
                          val heroRepository: HeroRepository,
                          val jewelryRepository: JewelryRepository) {

    @GetMapping("")
    fun getGears(): List<Gear> = gearRepository.findAll()

    @PostMapping("open/{type}")
    fun openGear(@ModelAttribute("player") player: Player, @PathVariable type: String): PlayerActionResponse {
        //type can be the type of core to open. ignored for now
        val gear = gearService.createGear(
            player.id,
            GearSet.values().toList(),
            Rarity.values().toList(),
            GearType.values().toList()
        )
        gearRepository.save(gear)
        return PlayerActionResponse(gear = gear)
    }
}

