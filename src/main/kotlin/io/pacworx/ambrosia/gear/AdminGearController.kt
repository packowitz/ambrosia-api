package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/gear")
class AdminGearController(val gearService: GearService,
                          val gearRepository: GearRepository,
                          val propertyService: PropertyService) {

    @GetMapping("")
    fun getGears(): List<Gear> = gearRepository.findAll()

    @PostMapping("open/{set}/{amount}")
    @Transactional
    fun openGear(@ModelAttribute("player") player: Player, @PathVariable set: GearSet, @PathVariable amount: Int): PlayerActionResponse {
        (1..amount).forEach { _ ->
            gearRepository.save(gearService.createGear(
                player.id,
                listOf(set),
                GearType.values().toList()
            ))
        }
        return PlayerActionResponse(gears = gearRepository.findAllByPlayerIdAndEquippedToIsNull(player.id))
    }

    @PostMapping("create")
    @Transactional
    fun createSpecificGear(@ModelAttribute("player") player: Player, @Valid @RequestBody request: CreateGearRequest): PlayerActionResponse {
        if (!player.admin) {
            throw RuntimeException("not allowed")
        }

        val valueRange = propertyService.getGearValueRange(request.type, request.rarity, request.stat)
        val statValue = valueRange.first + ((request.statQuality * (valueRange.second - valueRange.first)) / 100)

        val gear = Gear(
            playerId = player.id,
            set = request.set,
            rarity = request.rarity,
            type = request.type,
            stat = request.stat,
            statValue = statValue,
            statQuality = request.statQuality,
            jewelSlot1 = request.jewelSlot1,
            jewelSlot2 = request.jewelSlot2,
            jewelSlot3 = request.jewelSlot3,
            jewelSlot4 = request.jewelSlot4,
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
        val statQuality: Int,
        var jewelSlot1: GearJewelSlot?,
        val jewelSlot2: GearJewelSlot?,
        val jewelSlot3: GearJewelSlot?,
        val jewelSlot4: GearJewelSlot?,
        val specialJewelSlot: Boolean = false
    )
}

