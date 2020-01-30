package io.pacworx.ambrosia.loot

import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.GearService
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class LootService(private val lootBoxRepository: LootBoxRepository,
                  private val resourcesService: ResourcesService,
                  private val heroService: HeroService,
                  private val gearLootRepository: GearLootRepository,
                  private val gearService: GearService) {

    fun openLootBox(player: Player, lootBoxId: Long): LootBoxResult {
        val lootBox = lootBoxRepository.findByIdOrNull(lootBoxId)
            ?: throw RuntimeException("Unknown LootBox #$lootBoxId")
        return openLootBox(player, lootBox)
    }

    fun openLootBox(player: Player, lootBox: LootBox): LootBoxResult {
        var slotOpened = -1
        val items: MutableList<LootItemResult> = mutableListOf()
        lootBox.items.forEach { item ->
            if (item.slotNumber != slotOpened && procs(item.chance)) {
                slotOpened = item.slotNumber
                items.add(openLootItem(player, item))
            }
        }
        return LootBoxResult(
            lootBoxId = lootBox.id,
            items = items
        )
    }

    fun openLootItem(player: Player, item: LootItem): LootItemResult {
        return when(item.type) {
            LootItemType.RESOURCE -> LootItemResult(resource = openResourceItem(player, item))
            LootItemType.HERO -> LootItemResult(hero = openHeroItem(player, item))
            LootItemType.GEAR -> LootItemResult(gear = openGearItem(player, item))
        }
    }

    private fun openResourceItem(player: Player, item: LootItem): ResourceLoot {
        resourcesService.gainResources(player, item.resourceType!!, item.resourceAmount!!)
        return ResourceLoot(item.resourceType!!, item.resourceAmount!!)
    }

    private fun openHeroItem(player: Player, item: LootItem): HeroDto {
        return heroService.recruitHero(player, item.heroBaseId!!)
    }

    private fun openGearItem(player: Player, item: LootItem): Gear {
        val gearLoot = gearLootRepository.findByIdOrNull(item.gearLootId)
            ?: throw RuntimeException("Unknown gear loot #${item.gearLootId}")
        return gearService.createGear(
            player.id,
            gearLoot.getSets(),
            gearLoot.getTypes(),
            gearLoot.legendaryChance,
            gearLoot.epicChance,
            gearLoot.rareChance,
            gearLoot.uncommonChance,
            gearLoot.commonChance,
            gearLoot.jewel1chance,
            gearLoot.jewel2chance,
            gearLoot.jewel3chance,
            gearLoot.jewel4chance,
            gearLoot.specialJewelChance
        )
    }

}

data class LootBoxResult(
    val lootBoxId: Long,
    val items: List<LootItemResult>
)

data class LootItemResult(
    val resource: ResourceLoot? = null,
    val hero: HeroDto? = null,
    val gear: Gear? = null
)

data class ResourceLoot(
    val type: ResourceType,
    val amount: Int
)
