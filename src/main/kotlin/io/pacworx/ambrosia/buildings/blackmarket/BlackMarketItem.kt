package io.pacworx.ambrosia.buildings.blackmarket

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.loot.LootableBox
import io.pacworx.ambrosia.loot.LootableItem
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.resources.ResourceType
import javax.persistence.*

@Entity
data class BlackMarketItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val active: Boolean,
    val sortOrder: Int,
    val lootBoxId: Long,
    @Enumerated(EnumType.STRING)
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val priceType: ResourceType,
    val priceAmount: Int
) {
    @Transient
    var lootableItem: LootableItem? = null
}