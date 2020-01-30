package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.*

@Entity
data class LootItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val slotNumber: Int,
    val itemOrder: Int,
    val chance: Int,
    @Enumerated(EnumType.STRING)
    val type: LootItemType,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var resourceType: ResourceType? = null,
    var resourceAmount: Int? = null,
    var heroBaseId: Long? = null,
    var gearLootId: Long? = null
)
