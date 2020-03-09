package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.enums.Color
import io.pacworx.ambrosia.enums.GearSet
import io.pacworx.ambrosia.enums.JewelType
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType
import javax.persistence.*

@Entity
data class LootItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val slotNumber: Int,
    val itemOrder: Int,
    val chance: Int,
    @Enumerated(EnumType.STRING)
    val color: Color?,
    @Enumerated(EnumType.STRING)
    val type: LootItemType,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var resourceType: ResourceType? = null,
    var resourceFrom: Int? = null,
    var resourceTo: Int? = null,
    var heroBaseId: Long? = null,
    var heroLevel: Int? = null,
    var gearLootId: Long? = null,
    @JsonIgnore var jewelTypeNames: String?,
    var jewelLevel: Int? = null,
    var vehicleBaseId: Long? = null,
    var vehiclePartType: PartType? = null,
    var vehiclePartQuality: PartQuality? = null
    ) {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    fun getJewelTypes(): List<JewelType> {
        return jewelTypeNames?.takeIf { it.isNotEmpty() }?.split(";")?.map { JewelType.valueOf(it) } ?: listOf()
    }

    fun setJewelTypes(types: List<JewelType>) {
        this.jewelTypeNames = types.joinToString(separator = ";")
    }
}
