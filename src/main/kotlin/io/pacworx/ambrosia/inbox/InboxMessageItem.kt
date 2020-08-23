package io.pacworx.ambrosia.inbox

import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.progress.ProgressStat
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType
import javax.persistence.*

@Entity
data class InboxMessageItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val number: Int,
    @Enumerated(EnumType.STRING)
    val type: LootItemType,
    @Enumerated(EnumType.STRING)
    val resourceType: ResourceType? = null,
    val resourceAmount: Int? = null,
    val heroBaseId: Long? = null,
    val heroLevel: Int? = null,
    @Enumerated(EnumType.STRING)
    val jewelType: JewelType? = null,
    val jewelLevel: Int? = null,
    val vehicleBaseId: Long? = null,
    @Enumerated(EnumType.STRING)
    val vehiclePartType: PartType? = null,
    @Enumerated(EnumType.STRING)
    val vehiclePartQuality: PartQuality? = null,
    @Enumerated(EnumType.STRING)
    val progressStat: ProgressStat? = null,
    val progressBonus: Int? = null
)