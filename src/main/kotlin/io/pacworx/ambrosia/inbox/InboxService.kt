package io.pacworx.ambrosia.inbox

import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.loot.*
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehicleStat
import org.springframework.stereotype.Service
import kotlin.math.round
import kotlin.random.Random

@Service
class InboxService(
    private val resourcesService: ResourcesService,
    private val heroService: HeroService
) {

    fun claimInboxItem(player: Player, item: InboxMessageItem): LootedItem {
        return when(item.type) {
            LootItemType.RESOURCE -> claimResourceItem(player, item)
            LootItemType.HERO -> LootItemResult(hero = openHeroItem(player, item))
            LootItemType.JEWEL -> LootItemResult(jewelry = openJewelItem(player, item), jewelLevel = item.jewelLevel)
            LootItemType.VEHICLE -> LootItemResult(vehicle = openVehicleItem(player, item))
            LootItemType.VEHICLE_PART -> LootItemResult(vehiclePart = openVehiclePartItem(player, item))
            LootItemType.PROGRESS -> LootItemResult(progress = openProgressItem(player, item))
        }
    }

    private fun claimResourceItem(player: Player, item: InboxMessageItem): LootedItem {
        resourcesService.gainResources(player, item.resourceType!!, item.resourceAmount!!)
        return LootedItem(LootItemType.RESOURCE, resourceType = item.resourceType, value = item.resourceAmount.toLong())
    }

    private fun claimHeroItem(player: Player, item: InboxMessageItem): LootedItem {
        val hero = heroService.recruitHero(player, item.heroBaseId!!, item.heroLevel ?: 1)
        return LootedItem(LootItemType.HERO, value = hero.id)
    }


}