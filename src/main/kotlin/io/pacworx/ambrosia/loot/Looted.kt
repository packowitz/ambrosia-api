package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.resources.ResourceType

data class Looted(
    val type: LootItemType,
    @JsonFormat(shape = JsonFormat.Shape.STRING) val resourceType: ResourceType?,
    val value: Long
) {
    constructor(item: LootItemResult): this(
        type = when {
            item.resource != null -> LootItemType.RESOURCE
            item.hero != null -> LootItemType.HERO
            item.gear != null -> LootItemType.GEAR
            else -> throw RuntimeException("LootItemType not resolvable")
        },
        resourceType = item.resource?.type,
        value = when {
            item.resource != null -> item.resource.amount.toLong()
            item.hero != null -> item.hero.id
            item.gear != null -> item.gear.id
            else -> throw RuntimeException("LootItemType not resolvable")
        }
    )
}
