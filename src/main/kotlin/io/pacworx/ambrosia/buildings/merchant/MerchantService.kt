package io.pacworx.ambrosia.buildings.merchant

import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.gear.GearRepository
import io.pacworx.ambrosia.gear.GearService
import io.pacworx.ambrosia.loot.*
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.progress.ProgressRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class MerchantService(
    private val merchantPlayerItemRepository: MerchantPlayerItemRepository,
    private val merchantItemRepository: MerchantItemRepository,
    private val progressRepository: ProgressRepository,
    private val gearRepository: GearRepository,
    private val gearService: GearService,
    private val gearLootRepository: GearLootRepository,
    private val lootBoxRepository: LootBoxRepository
) {

    fun getItems(player: Player, progress: Progress = progressRepository.getOne(player.id), forceRenew: Boolean = false): List<MerchantPlayerItem> {
        if (progress.merchantLevel == 0) {
            return emptyList()
        }
        var items = merchantPlayerItemRepository.findAllByPlayerIdOrderBySortOrder(player.id)
        if (forceRenew || items.isEmpty() || !isToday(items[0].created) || items.any { it.merchantLevel < progress.merchantLevel }) {
            items = renewItems(player, progress, items)
        }
        items.filter { it.gearId != null }.forEach { it.gear = gearRepository.getOne(it.gearId!!) }
        return items
    }

    private fun renewItems(player: Player, progress: Progress, oldItems: List<MerchantPlayerItem>): List<MerchantPlayerItem> {
        oldItems.filter { !it.sold && it.gearId != null }.forEach {
            gearRepository.deleteById(it.gearId!!)
        }
        merchantPlayerItemRepository.deleteAll(oldItems)
        return merchantItemRepository.findAllByMerchantLevelOrderBySortOrder(progress.merchantLevel).map { merchantItem ->
            val lootBox = lootBoxRepository.findByIdOrNull(merchantItem.lootBoxId)
                ?: throw EntityNotFoundException(player, "loot box", merchantItem.lootBoxId)
            val lootItem = getLootItem(player, lootBox)
            val item = MerchantPlayerItem(
                playerId = player.id,
                sortOrder = merchantItem.sortOrder,
                merchantLevel = merchantItem.merchantLevel,
                priceType = merchantItem.priceType,
                priceAmount = merchantItem.priceAmount
            )
            when(lootItem.type) {
                LootItemType.RESOURCE -> {
                    item.resourceType = lootItem.resourceType
                    item.resourceAmount = Random.nextInt(lootItem.resourceFrom!!, lootItem.resourceTo!! + 1)
                }
                LootItemType.HERO -> {
                    item.heroBaseId = lootItem.heroBaseId
                    item.heroLevel = lootItem.heroLevel
                }
                LootItemType.GEAR -> {
                    val gearLoot = gearLootRepository.findByIdOrNull(lootItem.gearLootId)
                        ?: throw EntityNotFoundException(player, "gear loot", lootItem.gearLootId!!)
                    val gear = gearService.createGear(
                        null,
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
                        gearLoot.specialJewelChance,
                        gearLoot.statFrom,
                        gearLoot.statTo
                    )
                    item.gearId = gear.id
                }
                LootItemType.JEWEL -> {
                    item.jewelType = lootItem.getJewelTypes().random()
                    item.jewelLevel = lootItem.jewelLevel
                }
                LootItemType.VEHICLE -> {
                    item.vehicleBaseId = lootItem.vehicleBaseId
                }
                LootItemType.VEHICLE_PART -> {
                    item.vehiclePartType = lootItem.vehiclePartType
                    item.vehiclePartQuality = lootItem.vehiclePartQuality
                }
                LootItemType.PROGRESS -> throw ConfigurationException("Progress not allowed as purchasable loot item type")
            }

            merchantPlayerItemRepository.save(item)
        }
    }

    private fun getLootItem(player: Player, lootBox: LootBox): LootItem {
        var openRandom = Random.nextInt(100)
        var lootItem: LootItem? = null
        val items = lootBox.items.filter { it.slotNumber == 1 && it.color == null || it.color == player.color }
        items.forEach { slotItem ->
            if (lootItem == null && procs(slotItem.chance, openRandom)) {
                lootItem = slotItem
            } else {
                openRandom -= slotItem.chance
            }
        }
        return lootItem ?: items.last()
    }

    private fun isToday(timestamp: LocalDateTime): Boolean {
        val now = LocalDateTime.now()
        return timestamp.dayOfMonth == now.dayOfMonth &&
            timestamp.month == now.month &&
            timestamp.year == now.year
    }
}