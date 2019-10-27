package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.GearService
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.transaction.Transactional
import javax.validation.Valid
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

    @PostMapping("create")
    fun createSpecificGear(@ModelAttribute("player") player: Player, @Valid @RequestBody request: CreateGearRequest): PlayerActionResponse {
        if (!player.admin) {
            throw RuntimeException("not allowed")
        }
        val gear = Gear(
            playerId = player.id,
            set = request.set,
            rarity = request.rarity,
            type = request.type,
            stat = request.stat,
            statValue = request.statValue,
            jewelSlot1 = request.jewel1slot,
            jewelSlot2 = request.jewel2slot,
            jewelSlot3 = request.jewel3slot,
            jewelSlot4 = request.jewel4slot,
            specialJewelSlot = request.type == GearType.ARMOR && request.specialJewelSlot
        )
        gearRepository.save(gear)
        return PlayerActionResponse(gear = gear)
    }

    data class CreateGearRequest(
        val set: GearSet,
        val rarity: Rarity,
        val type: GearType,
        val stat: HeroStat,
        val statValue: Int,
        val jewel1slot: GearJewelSlot?,
        val jewel2slot: GearJewelSlot?,
        val jewel3slot: GearJewelSlot?,
        val jewel4slot: GearJewelSlot?,
        val specialJewelSlot: Boolean = false
    )
}

