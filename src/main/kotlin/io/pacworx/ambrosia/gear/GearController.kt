package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.HeroBusyException
import io.pacworx.ambrosia.exceptions.InsufficientResourcesException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("gear")
class GearController(
    private val gearRepository: GearRepository,
    private val jewelryRepository: JewelryRepository,
    private val heroRepository: HeroRepository,
    private val heroService: HeroService,
    private val auditLogService: AuditLogService,
    private val gearService: GearService
) {

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
            throw UnauthorizedException(player, "You are only allowed to do actions on your own heroes")
        }
        if (hero.stars < gear.rarity.stars) {
            throw GeneralException(player, "Hero too weak", "You cannot equip gear of higher rarity than the hero stars has")
        }
        if (gear.equippedTo != null) {
            throw GeneralException(player, "Already equipped", "You cannot equip a gear that is already equipped")
        }
        val unequipped = hero.equip(gear)
        auditLogService.log(player, "Equips ${gear.rarity.stars}* ${gear.set.name} ${gear.type.name} ${gear.statValue} ${gear.stat.name} #${gear.id} " +
                "to hero $${hero.heroBase.name} #${hero.id}${unequipped?.let { " unequipping gear #${it.id}" } ?: ""}"
        )
        val jewelries: MutableList<Jewelry> = mutableListOf()
        unequipped?.let { gearService.unplugJewels(player, jewelries, it) }
        return PlayerActionResponse(
            hero = heroService.asHeroDto(hero),
            gear = unequipped,
            gearIdsRemovedFromArmory = listOf(gear.id),
            jewelries = jewelries.takeIf { it.isNotEmpty() }
        )
    }

    @PostMapping("unequip")
    @Transactional
    fun unequipGear(@ModelAttribute("player") player: Player, @RequestBody request: EquipRequest): PlayerActionResponse {
        val hero = heroRepository.getOne(request.heroId)
        val gear = gearRepository.getOne(request.gearId)
        if (hero.playerId != player.id || gear.playerId != player.id || gear.equippedTo != hero.id) {
            throw UnauthorizedException(player, "You are only allowed to do actions on your own heroes")
        }
        val unequipped = hero.unequip(gear.type)
        auditLogService.log(player, "Unequips ${gear.rarity.stars}* ${gear.set.name} ${gear.type.name} ${gear.statValue} ${gear.stat.name} #${gear.id} " +
                "from hero $${hero.heroBase.name} #${hero.id}"
        )
        val jewelries: MutableList<Jewelry> = mutableListOf()
        unequipped?.let { gearService.unplugJewels(player, jewelries, it) }
        return PlayerActionResponse(
            hero = heroService.asHeroDto(hero),
            gear = unequipped,
            jewelries = jewelries.takeIf { it.isNotEmpty() }
        )
    }


    @PostMapping("plugin/jewel")
    @Transactional
    fun pluginJewel(@ModelAttribute("player") player: Player, @RequestBody request: PluginJewelRequest): PlayerActionResponse {
        val gear = gearRepository.getOne(request.gearId)
        if (gear.playerId != player.id) {
            throw UnauthorizedException(player, "You are only allowed to do actions on your own gear")
        }
        val gearJewelSlot = gear.getJewelSlot(request.slot)
                ?: throw GeneralException(player, "Wrong gear", "Gear doesn't have slot ${request.slot}")
        if (request.jewelType.slot != gearJewelSlot) {
            throw GeneralException(player, "Wrong jewel", "Cannot plugin ${request.jewelType} into $gearJewelSlot")
        }
        var unpluggedJewel: Pair<JewelType, Int>? = null
        val unpluggedJewelry: Jewelry? = gear.getJewel(request.slot)?.let {
            unpluggedJewel = it
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
            throw InsufficientResourcesException(player.id, request.jewelType.name, 1)
        }
        pluggedJewelry.increaseAmount(request.lvl, -1)
        gear.pluginJewel(request.slot, request.jewelType, request.lvl)
        gearRepository.save(gear)

        val modifiedJewelries = mutableListOf(pluggedJewelry)
        if (unpluggedJewelry != null && unpluggedJewelry.type != pluggedJewelry.type) {
            modifiedJewelries.add(unpluggedJewelry)
        }

        auditLogService.log(player, "Plugin lvl ${request.lvl} ${request.jewelType.name} jewel to gear #${gear.id}" +
                "${unpluggedJewel?.let { " taking out lvl ${it.second} ${it.first.name} jewel" } ?: ""}"
        )

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
            throw UnauthorizedException(player, "You are only allowed to do actions on your own gear")
        }

        var unpluggedJewel: Pair<JewelType, Int>? = null
        val unpluggedJewelry: Jewelry? = gear.getJewel(request.slot)?.let {
            unpluggedJewel = it
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

        auditLogService.log(player, "${unpluggedJewel?.let{ "Taking out lvl ${it.second} ${it.first.name} jewel from gear #${gear.id}" } ?: "No jewel to out of gear #${gear.id}"}")

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
