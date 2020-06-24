package io.pacworx.ambrosia.loot

data class Looted (
    val type: LootedType,
    val items: List<LootedItem>
)