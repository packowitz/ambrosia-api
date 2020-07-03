package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.gear.Jewelry
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.loot.LootedType
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional
import kotlin.math.round
import kotlin.random.Random

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("forge")
class ForgeController(
    val gearRepository: GearRepository,
    val progressRepository: ProgressRepository,
    val propertyService: PropertyService,
    val resourcesService: ResourcesService,
    val auditLogService: AuditLogService,
    val jewelryRepository: JewelryRepository,
    val oddJobService: OddJobService
) {

    @PostMapping("breakdown")
    @Transactional
    fun breakDown(@ModelAttribute("player") player: Player,
                  @RequestBody request: BreakDownRequest): PlayerActionResponse {
        val gears = gearRepository.findAllById(request.gearIds)
        val progress = progressRepository.getOne(player.id)
        gears.find { it.equippedTo != null }?.let { throw GeneralException(player, "Cannot breakdown gear", "Gear is equipped") }
        gears.find { it.rarity.stars > progress.gearBreakDownRarity }?.let { throw GeneralException(player, "Cannot breakdown gear", "Gear rarity is too high. Upgrade Forge.") }
        gears.find { it.modificationInProgress }?.let { throw GeneralException(player, "Cannot breakdown gear", "Gear modification is in progress") }

        val jewelries: MutableList<Jewelry> = mutableListOf()
        gears.forEach { unplugJewels(player, jewelries, it) }

        var resources: Resources? = null

        val lootedItems = mutableListOf<LootedItem>()
        gears.map { gear ->
            propertyService.getProperties(resolveBreakdownPropertyType(gear), gear.rarity.stars).map { prop ->
                val amount = round(Random.nextInt(prop.value1, prop.value2!! + 1) * (100.0 + progress.gearBreakDownResourcesInc) / 100).toInt()
                resources = resourcesService.gainResources(player, prop.resourceType!!, amount)
                val loot = lootedItems.find { it.resourceType ==  prop.resourceType}
                if (loot != null) {
                    loot.value += amount.toLong()
                } else {
                    lootedItems.add(LootedItem(
                        type = LootItemType.RESOURCE,
                        resourceType = prop.resourceType,
                        value = amount.toLong()))
                }
            }
        }

        auditLogService.log(player,"Breaking down ${gears.joinToString { "${it.rarity.stars}* ${it.type.name} #${it.id}" } }" +
                "gaining ${lootedItems.joinToString { "${it.value} ${it.resourceType?.name}" }}"
        )

        val oddJobsEffected = oddJobService.gearBreakDownGear(player, gears)
        gearRepository.deleteAll(gears)
        return PlayerActionResponse(
            resources = resources,
            gearIdsRemovedFromArmory = gears.map { it.id },
            looted = Looted(LootedType.BREAKDOWN, lootedItems),
            jewelries = jewelries,
            oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() }
        )
    }

    private fun unplugJewels(player: Player, jewelries: MutableList<Jewelry>, gear: Gear) {
        (0..4).forEach { slotNumber ->
            gear.getJewel(slotNumber)?.let { jewel ->
                val jewelry = jewelries.find { it.type == jewel.first }
                    ?: jewelryRepository.findByPlayerIdAndType(player.id, jewel.first)
                    ?: jewelryRepository.save(Jewelry(playerId = player.id, type = jewel.first))
                jewelry.increaseAmount(jewel.second, 1)
                gear.unplugJewel(slotNumber)
            }
        }
    }

    private fun resolveBreakdownPropertyType(gear: Gear): PropertyType {
        if (gear.jewelSlot4 != null) {
            return if (gear.specialJewelSlot) {
                PropertyType.FORGE_BREAKDOWN_5_JEWEL
            } else {
                PropertyType.FORGE_BREAKDOWN_4_JEWEL
            }
        }
        if (gear.jewelSlot3 != null) {
            return if (gear.specialJewelSlot) {
                PropertyType.FORGE_BREAKDOWN_4_JEWEL
            } else {
                PropertyType.FORGE_BREAKDOWN_3_JEWEL
            }
        }
        if (gear.jewelSlot2 != null) {
            return if (gear.specialJewelSlot) {
                PropertyType.FORGE_BREAKDOWN_3_JEWEL
            } else {
                PropertyType.FORGE_BREAKDOWN_2_JEWEL
            }
        }
        if (gear.jewelSlot1 != null) {
            return if (gear.specialJewelSlot) {
                PropertyType.FORGE_BREAKDOWN_2_JEWEL
            } else {
                PropertyType.FORGE_BREAKDOWN_1_JEWEL
            }
        }
        return if (gear.specialJewelSlot) {
            PropertyType.FORGE_BREAKDOWN_1_JEWEL
        } else {
            PropertyType.FORGE_BREAKDOWN_0_JEWEL
        }
    }

    data class BreakDownRequest(
        val gearIds: List<Long>
    )
}
