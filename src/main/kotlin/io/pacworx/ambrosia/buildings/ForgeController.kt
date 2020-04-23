package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import kotlin.math.round
import kotlin.random.Random

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("forge")
class ForgeController(val gearRepository: GearRepository,
                      val progressRepository: ProgressRepository,
                      val propertyService: PropertyService,
                      val resourcesService: ResourcesService) {

    @PostMapping("breakdown")
    @Transactional
    fun breakDown(@ModelAttribute("player") player: Player,
                  @RequestBody request: BreakDownRequest): PlayerActionResponse {
        val gears = gearRepository.findAllById(request.gearIds)
        val progress = progressRepository.getOne(player.id)
        if (gears.any { it.equippedTo != null } || gears.any { it.rarity.stars > progress.gearBreakDownRarity } || gears.any { it.modificationInProgress }) {
            throw RuntimeException("Cannot break down gear")
        }
        var resources: Resources? = null

        val looted = mutableListOf<Looted>()
        gears.map { gear ->
            propertyService.getProperties(resolveBreakdownPropertyType(gear), gear.rarity.stars).map { prop ->
                val amount = round(Random.nextInt(prop.value1, prop.value2!! + 1) * (100.0 + progress.gearBreakDownResourcesInc) / 100).toInt()
                resources = resourcesService.gainResources(player, prop.resourceType!!, amount)
                val loot = looted.find { it.resourceType ==  prop.resourceType}
                if (loot != null) {
                    loot.value += amount.toLong()
                } else {
                    looted.add(Looted(
                        type = LootItemType.RESOURCE,
                        resourceType = prop.resourceType,
                        value = amount.toLong()))
                }
            }
        }

        gearRepository.deleteAll(gears)
        return PlayerActionResponse(
            resources = resources,
            gearIdsRemovedFromArmory = gears.map { it.id },
            looted = looted
        )
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
