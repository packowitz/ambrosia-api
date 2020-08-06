package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.progress.ProgressStat
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType
import javax.persistence.EnumType
import javax.persistence.Enumerated

data class LootableItem (
    @Enumerated(EnumType.STRING)
    val type: LootItemType,
    @Enumerated(EnumType.STRING)
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    var resourceType: ResourceType? = null,
    var resourceAmount: Int? = null,
    var progressStat: ProgressStat? = null,
    var progressBonus: Int? = null,
    var heroBaseId: Long? = null,
    var heroLevel: Int? = null,
    @JsonIgnore
    var gearId: Long? = null,
    @Enumerated(EnumType.STRING)
    var jewelType: JewelType? = null,
    var jewelLevel: Int? = null,
    var vehicleBaseId: Long? = null,
    @Enumerated(EnumType.STRING)
    var vehiclePartType: PartType? = null,
    @Enumerated(EnumType.STRING)
    var vehiclePartQuality: PartQuality? = null
) {

    @Transient
    var gear: Gear? = null
}