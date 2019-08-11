package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

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
    fun openGear(@PathVariable type: String): Gear {
        //type can be the type of core to open. ignored for now
        val gear = gearService.createGear(
            1,
            GearSet.values().toList(),
            Rarity.values().toList(),
            GearType.values().toList()
        )
        gearRepository.save(gear)
        return gear
    }

    @PostMapping("equip")
    @Transactional
    fun equipGear(@RequestBody request: EquipRequest): EquipResponse {
        val hero = heroRepository.getOne(request.heroId)
        val gear = gearRepository.getOne(request.gearId)
        if (hero.stars < gear.rarity.stars) {
            throw RuntimeException("You cannot equip gear of higher rarity than the hero stars has")
        }
        val unequipped = hero.equip(gear)
        return EquipResponse(hero, gear.id!!, unequipped)
    }


    @PostMapping("plugin/jewel")
    @Transactional
    fun pluginJewel(@RequestBody request: PluginJewelRequest): PluginJewelResponse {
        val gear = gearRepository.getOne(request.gearId)
        val gearJewelSlot = gear.getJewelSlot(request.slot) ?: throw RuntimeException("Gear doesn't have slot ${request.slot}")
        if (request.jewelType.slot != gearJewelSlot) {
            throw RuntimeException("Cannot plugin ${request.jewelType} into $gearJewelSlot")
        }
        val unpluggedJewelry: Jewelry? = gear.getJewel(request.slot)?.let {
            val jewelry = jewelryRepository.findByPlayerIdAndType(1, it.first) ?: jewelryRepository.save(Jewelry(playerId = 1, type = it.first))
            jewelry.increaseAmount(it.second, 1)
            jewelry
        }
        val pluggedJewelry = unpluggedJewelry?.takeIf { it.type == request.jewelType } ?: run {
            jewelryRepository.findByPlayerIdAndType(1, request.jewelType) ?: Jewelry(playerId = 1, type = request.jewelType)
        }
        if (pluggedJewelry.getAmount(request.lvl) < 1) {
            throw RuntimeException("You don't have any jewel ${request.jewelType} level ${request.lvl}")
        }
        pluggedJewelry.increaseAmount(request.lvl, 1)
        gear.pluginJewel(request.slot, request.jewelType, request.lvl)
        gearRepository.save(gear)
        val hero = gear.takeIf { it.equippedTo != null }?.let { heroRepository.getOne(it.equippedTo!!) }
        val modifiedJewelries = mutableListOf(pluggedJewelry)
        if (unpluggedJewelry != null && unpluggedJewelry.type != pluggedJewelry.type) {
            modifiedJewelries.add(unpluggedJewelry)
        }
        return PluginJewelResponse(hero, gear, modifiedJewelries)
    }
}

data class EquipRequest(val heroId: Long, val gearId: Long)

data class EquipResponse(val hero: Hero, val equipped: Long, val unequipped: Gear?)

data class PluginJewelRequest(val gearId: Long, val slot: Int, val jewelType: JewelType, val lvl: Int)

data class PluginJewelResponse(val hero: Hero?, val gear: Gear, val jewelries: List<Jewelry>)

