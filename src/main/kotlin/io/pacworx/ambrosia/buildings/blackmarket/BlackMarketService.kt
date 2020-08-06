package io.pacworx.ambrosia.buildings.blackmarket

import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.player.Player
import org.springframework.stereotype.Service

@Service
class BlackMarketService(
    private val blackMarketItemRepository: BlackMarketItemRepository,
    private val lootService: LootService
) {

    fun getPurchasableItems(player: Player): List<BlackMarketItem> {
        val items = blackMarketItemRepository.findAllByActiveIsTrueOrderBySortOrderAsc()
        items.forEach { item ->
            item.lootableItem = lootService.getLootableBox(player, item.lootBoxId).items.first()
        }
        return items
    }}