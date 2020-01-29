package io.pacworx.ambrosia.loot

import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class LootItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val slotNumber: Int,
    val itemOrder: Int,
    val chance: Int,
    val type: LootItemType,
    val resourceType: ResourceType? = null,
    val resourceAmount: Int? = null,
    val heroBaseId: Long? = null,
    val gearLootId: Long? = null
)
