package io.pacworx.ambrosia.inbox

import io.pacworx.ambrosia.gear.Jewelry
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.VehicleService
import org.springframework.stereotype.Service
import javax.naming.ConfigurationException

@Service
class InboxService(
    private val resourcesService: ResourcesService,
    private val heroService: HeroService,
    private val jewelryRepository: JewelryRepository,
    private val vehicleService: VehicleService,
    private val progressRepository: ProgressRepository
) {

    fun claimInboxItem(player: Player, item: InboxMessageItem): LootedItem {
        return when(item.type) {
            LootItemType.RESOURCE -> claimResourceItem(player, item)
            LootItemType.HERO -> claimHeroItem(player, item)
            LootItemType.GEAR -> throw ConfigurationException("GEAR is not allowed as inboxMessageItem")
            LootItemType.JEWEL -> claimJewelItem(player, item)
            LootItemType.VEHICLE -> claimVehicleItem(player, item)
            LootItemType.VEHICLE_PART -> claimVehiclePartItem(player, item)
            LootItemType.PROGRESS -> claimProgressItem(player, item)
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

    private fun claimJewelItem(player: Player, item: InboxMessageItem): LootedItem {
        val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, item.jewelType!!)
                ?: jewelryRepository.save(Jewelry(playerId = player.id, type = item.jewelType))
        jewelry.increaseAmount(item.jewelLevel!!, 1)
        return LootedItem(LootItemType.JEWEL, jewelType = item.jewelType, value = item.jewelLevel.toLong())
    }

    private fun claimVehicleItem(player: Player, item: InboxMessageItem): LootedItem {
        val vehicle = vehicleService.gainVehicle(player, item.vehicleBaseId!!)
        return LootedItem(LootItemType.VEHICLE, value = vehicle.id)
    }

    private fun claimVehiclePartItem(player: Player, item: InboxMessageItem): LootedItem {
        val part = vehicleService.gainVehiclePart(player, item.vehiclePartType!!, item.vehiclePartQuality!!)
        return LootedItem(LootItemType.VEHICLE_PART, value = part.id)
    }

    private fun claimProgressItem(player: Player, item: InboxMessageItem): LootedItem {
        val progress = progressRepository.getOne(player.id)
        item.progressStat!!.apply(progress, item.progressBonus!!)
        return LootedItem(LootItemType.PROGRESS, progressStat = item.progressStat, value = item.progressBonus.toLong())
    }
}