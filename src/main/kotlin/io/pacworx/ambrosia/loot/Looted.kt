package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.resources.ResourceType

data class Looted(
    val type: LootItemType,
    @JsonFormat(shape = JsonFormat.Shape.STRING) val resourceType: ResourceType?,
    @JsonFormat(shape = JsonFormat.Shape.OBJECT) val jewelType: JewelType? = null,
    var value: Long
)
