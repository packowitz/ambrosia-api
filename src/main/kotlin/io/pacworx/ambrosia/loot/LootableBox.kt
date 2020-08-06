package io.pacworx.ambrosia.loot

data class LootableBox (
    val type: LootBoxType,
    val lootBoxId: Long,
    val items: List<LootableItem>
)
