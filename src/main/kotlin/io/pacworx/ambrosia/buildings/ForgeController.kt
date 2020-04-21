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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("breakdown/{gearId}")
    @Transactional
    fun breakDown(@ModelAttribute("player") player: Player,
               @PathVariable gearId: Long): PlayerActionResponse {
        val gear = gearRepository.findByIdOrNull(gearId)
            ?: throw RuntimeException("Unknown gear")
        val progress = progressRepository.getOne(player.id)
        if (gear.equippedTo != null || gear.rarity.stars > progress.gearBreakDownRarity || gear.modificationInProgress) {
            throw RuntimeException("Cannot break down gear")
        }
        var resources: Resources? = null

        val looted = propertyService.getProperties(resolveBreakdownPropertyType(gear), gear.rarity.stars).map {
            val amount = round(Random.nextInt(it.value1, it.value2!! + 1) * (100.0 + progress.gearBreakDownResourcesInc) / 100).toInt()
            resources = resourcesService.gainResources(player,  it.resourceType!!, amount)
            Looted(
                type = LootItemType.RESOURCE,
                resourceType = it.resourceType,
                value = amount.toLong()
            )
        }

        gearRepository.delete(gear)
        return PlayerActionResponse(
            resources = resources,
            gearIdsRemovedFromArmory = listOf(gear.id),
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
}
