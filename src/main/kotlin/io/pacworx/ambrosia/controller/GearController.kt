package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("gear")
class GearController(private val gearRepository: GearRepository,
                     private val jewelryRepository: JewelryRepository,
                     private val heroRepository: HeroRepository,
                     private val heroService: HeroService) {

    @GetMapping
    fun getAllGear(@ModelAttribute("player") player: Player): List<Gear> {
        return gearRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
    }

    @PostMapping("equip")
    @Transactional
    fun equipGear(@ModelAttribute("player") player: Player, @RequestBody request: EquipRequest): PlayerActionResponse {
        val hero = heroRepository.getOne(request.heroId)
        val gear = gearRepository.getOne(request.gearId)
        if (hero.playerId != player.id || gear.playerId != player.id) {
            throw RuntimeException("You are only allowed to do actions on your own heroes")
        }
        if (hero.stars < gear.rarity.stars) {
            throw RuntimeException("You cannot equip gear of higher rarity than the hero stars has")
        }
        val unequipped = hero.equip(gear)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero), gear = unequipped, gearIdsRemovedFromArmory = listOf(gear.id))
    }

    @PostMapping("unequip")
    @Transactional
    fun unequipGear(@ModelAttribute("player") player: Player, @RequestBody request: EquipRequest): PlayerActionResponse {
        val hero = heroRepository.getOne(request.heroId)
        val gear = gearRepository.getOne(request.gearId)
        if (hero.playerId != player.id || gear.playerId != player.id || gear.equippedTo != hero.id) {
            throw RuntimeException("You are only allowed to do actions on your own heroes")
        }
        val unequipped = hero.unequip(gear.type)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero), gear = unequipped)
    }


    @PostMapping("plugin/jewel")
    @Transactional
    fun pluginJewel(@ModelAttribute("player") player: Player, @RequestBody request: PluginJewelRequest): PlayerActionResponse {
        val gear = gearRepository.getOne(request.gearId)
        if (gear.playerId != player.id) {
            throw RuntimeException("You are only allowed to do actions on your own heroes")
        }
        val gearJewelSlot = gear.getJewelSlot(request.slot)
                ?: throw RuntimeException("Gear doesn't have slot ${request.slot}")
        if (request.jewelType.slot != gearJewelSlot) {
            throw RuntimeException("Cannot plugin ${request.jewelType} into $gearJewelSlot")
        }
        val unpluggedJewelry: Jewelry? = gear.getJewel(request.slot)?.let {
            val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, it.first)
                    ?: jewelryRepository.save(Jewelry(playerId = player.id, type = it.first))
            jewelry.increaseAmount(it.second, 1)
            jewelry
        }
        val pluggedJewelry = unpluggedJewelry?.takeIf { it.type == request.jewelType } ?: run {
            jewelryRepository.findByPlayerIdAndType(player.id, request.jewelType)
                    ?: Jewelry(playerId = player.id, type = request.jewelType)
        }
        if (pluggedJewelry.getAmount(request.lvl) < 1) {
            throw RuntimeException("You don't have any jewel ${request.jewelType} level ${request.lvl}")
        }
        pluggedJewelry.increaseAmount(request.lvl, -1)
        gear.pluginJewel(request.slot, request.jewelType, request.lvl)
        gearRepository.save(gear)

        val modifiedJewelries = mutableListOf(pluggedJewelry)
        if (unpluggedJewelry != null && unpluggedJewelry.type != pluggedJewelry.type) {
            modifiedJewelries.add(unpluggedJewelry)
        }

        return if (gear.equippedTo != null) {
            val hero = heroRepository.getOne(gear.equippedTo!!)
            PlayerActionResponse(hero = heroService.asHeroDto(hero), gear = gear, jewelries = modifiedJewelries)
        } else {
            PlayerActionResponse(gear = gear, jewelries = modifiedJewelries)
        }
    }

    @PostMapping("unplug/jewel")
    @Transactional
    fun unplugJewel(@ModelAttribute("player") player: Player, @RequestBody request: UnplugJewelRequest): PlayerActionResponse {
        val gear = gearRepository.getOne(request.gearId)
        if (gear.playerId != player.id) {
            throw RuntimeException("You are only allowed to do actions on your own heroes")
        }

        val unpluggedJewelry: Jewelry? = gear.getJewel(request.slot)?.let {
            val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, it.first)
                ?: jewelryRepository.save(Jewelry(playerId = player.id, type = it.first))
            jewelry.increaseAmount(it.second, 1)
            jewelry
        }
        gear.unplugJewel(request.slot)
        gearRepository.save(gear)

        val modifiedJewelries = mutableListOf<Jewelry>()
        if (unpluggedJewelry != null) {
            modifiedJewelries.add(unpluggedJewelry)
        }

        return if (gear.equippedTo != null) {
            val hero = heroRepository.getOne(gear.equippedTo!!)
            PlayerActionResponse(hero = heroService.asHeroDto(hero), gear = gear, jewelries = modifiedJewelries)
        } else {
            PlayerActionResponse(gear = gear, jewelries = modifiedJewelries)
        }
    }
}

data class EquipRequest(val heroId: Long, val gearId: Long)

data class PluginJewelRequest(val gearId: Long, val slot: Int, val jewelType: JewelType, val lvl: Int)

data class UnplugJewelRequest(val gearId: Long, val slot: Int)
