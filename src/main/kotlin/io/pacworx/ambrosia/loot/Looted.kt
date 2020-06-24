package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.progress.ProgressStat
import io.pacworx.ambrosia.resources.ResourceType

data class Looted(
    val type: LootItemType,
    @JsonFormat(shape = JsonFormat.Shape.STRING) val resourceType: ResourceType? = null,
    val progressStat: ProgressStat? = null,
    @JsonFormat(shape = JsonFormat.Shape.OBJECT) val jewelType: JewelType? = null,
    var value: Long
)
