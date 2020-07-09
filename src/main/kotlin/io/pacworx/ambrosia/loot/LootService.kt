package io.pacworx.ambrosia.loot

import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.GearService
import io.pacworx.ambrosia.gear.Jewelry
import io.pacworx.ambrosia.gear.JewelryRepository
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.progress.ProgressStat
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehiclePart
import io.pacworx.ambrosia.vehicle.VehicleService
import io.pacworx.ambrosia.vehicle.VehicleStat
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import kotlin.math.round
import kotlin.random.Random

@Service
class LootService(
    private val lootBoxRepository: LootBoxRepository,
    private val resourcesService: ResourcesService,
    private val heroService: HeroService,
    private val gearLootRepository: GearLootRepository,
    private val gearService: GearService,
    private val jewelryRepository: JewelryRepository,
    private val vehicleService: VehicleService,
    private val progressRepository: ProgressRepository
) {

    fun asLootedItem(item: LootItemResult): LootedItem =
        LootedItem(
            type = resolveLootType(item),
            resourceType = item.resource?.type,
            progressStat = item.progress?.type,
            jewelType = item.jewelry?.type,
            value = resolveLootValue(item)
        )

    private fun resolveLootType(item: LootItemResult): LootItemType =
        when {
            item.resource != null -> LootItemType.RESOURCE
            item.hero != null -> LootItemType.HERO
            item.gear != null -> LootItemType.GEAR
            item.jewelry != null -> LootItemType.JEWEL
            item.vehicle != null -> LootItemType.VEHICLE
            item.vehiclePart != null -> LootItemType.VEHICLE_PART
            item.progress != null -> LootItemType.PROGRESS
            else -> throw RuntimeException("LootItemType not resolvable")
        }

    private fun resolveLootValue(item: LootItemResult): Long =
        when {
            item.resource != null -> item.resource.amount.toLong()
            item.hero != null -> item.hero.id
            item.gear != null -> item.gear.id
            item.jewelry != null -> item.jewelLevel!!.toLong()
            item.vehicle != null -> item.vehicle.id
            item.vehiclePart != null -> item.vehiclePart.id
            item.progress != null -> item.progress.amount.toLong()
            else -> throw RuntimeException("LootItemType not resolvable")
        }

    fun openLootBox(player: Player, lootBoxId: Long, vehicle: Vehicle? = null): LootBoxResult {
        val lootBox = lootBoxRepository.findByIdOrNull(lootBoxId)
            ?: throw EntityNotFoundException(player, "loot box", lootBoxId)
        return openLootBox(player, lootBox, vehicle)
    }

    fun openLootBox(player: Player, lootBox: LootBox, vehicle: Vehicle? = null): LootBoxResult {
        val items: MutableList<LootItemResult> = mutableListOf()
        lootBox.items.groupBy { it.slotNumber }.forEach { _: Int, slotItems: List<LootItem> ->
            var openRandom = Random.nextInt(100)
            var proced = false
            slotItems.filter { it.color == null || it.color == player.color }.forEach { slotItem ->
                if (!proced && procs(slotItem.chance, openRandom)) {
                    items.add(openLootItem(player, slotItem, vehicle))
                    proced = true
                } else {
                    openRandom -= slotItem.chance
                }
            }
        }
        return LootBoxResult(
            lootBoxId = lootBox.id,
            items = items
        )
    }

    private fun openLootItem(player: Player, item: LootItem, vehicle: Vehicle?): LootItemResult {
        return when(item.type) {
            LootItemType.RESOURCE -> LootItemResult(resource = openResourceItem(player, item, vehicle))
            LootItemType.HERO -> LootItemResult(hero = openHeroItem(player, item))
            LootItemType.GEAR -> LootItemResult(gear = openGearItem(player, item))
            LootItemType.JEWEL -> LootItemResult(jewelry = openJewelItem(player, item), jewelLevel = item.jewelLevel)
            LootItemType.VEHICLE -> LootItemResult(vehicle = openVehicleItem(player, item))
            LootItemType.VEHICLE_PART -> LootItemResult(vehiclePart = openVehiclePartItem(player, item))
            LootItemType.PROGRESS -> LootItemResult(progress = openProgressItem(player, item))
        }
    }

    private fun openResourceItem(player: Player, item: LootItem, vehicle: Vehicle? = null): ResourceLoot {
        var amount =  Random.nextInt(item.resourceFrom!!, item.resourceTo!! + 1)
        amount += round(amount.toDouble() * vehicleService.getStat(vehicle, VehicleStat.BATTLE_RESSOURCE_LOOT) / 100).toInt()
        resourcesService.gainResources(player, item.resourceType!!, amount)
        return ResourceLoot(item.resourceType!!, amount)
    }

    private fun openProgressItem(player: Player, item: LootItem): ProgressLoot {
        val progress = progressRepository.getOne(player.id)
        item.progressStat!!.apply(progress, item.progressStatBonus!!)
        return ProgressLoot(item.progressStat!!, item.progressStatBonus!!)
    }

    private fun openHeroItem(player: Player, item: LootItem): HeroDto {
        return heroService.recruitHero(player, item.heroBaseId!!, item.heroLevel ?: 1)
    }

    private fun openGearItem(player: Player, item: LootItem): Gear {
        val gearLoot = gearLootRepository.findByIdOrNull(item.gearLootId)
            ?: throw EntityNotFoundException(player, "gear loot", item.gearLootId ?: -1)
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

    private fun openJewelItem(player: Player, item: LootItem): Jewelry {
        item.getJewelTypes().random().let { type ->
            val jewelry = jewelryRepository.findByPlayerIdAndType(player.id, type) ?: Jewelry(playerId = player.id, type = type)
            jewelry.increaseAmount(item.jewelLevel!!, 1)
            return jewelryRepository.save(jewelry)
        }
    }

    private fun openVehicleItem(player: Player, item: LootItem): Vehicle {
        return vehicleService.gainVehicle(player, item.vehicleBaseId!!)
    }

    private fun openVehiclePartItem(player: Player, item: LootItem): VehiclePart {
        return vehicleService.gainVehiclePart(player, item.vehiclePartType!!, item.vehiclePartQuality!!)
    }

}

data class LootBoxResult(
    val lootBoxId: Long,
    val items: List<LootItemResult>
)

data class LootItemResult(
    val resource: ResourceLoot? = null,
    val hero: HeroDto? = null,
    val gear: Gear? = null,
    val jewelry: Jewelry? = null,
    val jewelLevel: Int? = null,
    val vehicle: Vehicle? = null,
    val vehiclePart: VehiclePart? = null,
    val progress: ProgressLoot? = null
) {
    fun auditLog(): String {
        return when {
            resource != null -> "${resource.amount} ${resource.type.name}"
            hero != null -> "hero ${hero.heroBase.name} #${hero.id} level ${hero.level}"
            gear != null -> "${gear.getGearQuality().name} (${gear.statQuality}%) ${gear.rarity.stars}* ${gear.set.name} ${gear.type.name} +${gear.statValue} ${gear.stat.name} with ${gear.getNumberOfJewelSlots()} jewels #${gear.id}"
            jewelry != null -> "lvl $jewelLevel ${jewelry.type.name} jewel"
            vehicle != null -> "vehicle ${vehicle.baseVehicle.name} #${vehicle.id}"
            vehiclePart != null -> "vehiclePart ${vehiclePart.quality.name} ${vehiclePart.type.name} #${vehiclePart.id}"
            progress != null -> "${progress.amount} ${progress.type.name}"
            else -> ""
        }
    }
}

data class ResourceLoot(
    val type: ResourceType,
    val amount: Int
)

data class ProgressLoot(
    val type: ProgressStat,
    val amount: Int
)
