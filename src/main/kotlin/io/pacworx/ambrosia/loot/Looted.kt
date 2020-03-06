package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.resources.ResourceType

data class Looted(
    val type: LootItemType,
    @JsonFormat(shape = JsonFormat.Shape.STRING) val resourceType: ResourceType?,
    @JsonFormat(shape = JsonFormat.Shape.OBJECT) val jewelType: JewelType?,
    val value: Long
) {
    constructor(item: LootItemResult): this(
        type = when {
            item.resource != null -> LootItemType.RESOURCE
            item.hero != null -> LootItemType.HERO
            item.gear != null -> LootItemType.GEAR
            item.jewelry != null -> LootItemType.JEWEL
            item.vehicle != null -> LootItemType.VEHICLE
            item.vehiclePart != null -> LootItemType.VEHICLE_PART
            else -> throw RuntimeException("LootItemType not resolvable")
        },
        resourceType = item.resource?.type,
        jewelType = item.jewelry?.type,
        value = when {
            item.resource != null -> item.resource.amount.toLong()
            item.hero != null -> item.hero.id
            item.gear != null -> item.gear.id
            item.jewelry != null -> item.jewelLevel!!.toLong()
            item.vehicle != null -> item.vehicle.id
            item.vehiclePart != null -> item.vehiclePart.id
            else -> throw RuntimeException("LootItemType not resolvable")
        }
    )
}
