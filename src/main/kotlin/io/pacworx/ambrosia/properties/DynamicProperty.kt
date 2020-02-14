package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.enums.HeroStat
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.vehicle.VehicleStat
import javax.persistence.*

@Entity
data class DynamicProperty(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val category: PropertyCategory,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val type: PropertyType,
    val level: Int?,
    @Enumerated(EnumType.STRING)
    val stat: HeroStat?,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val resourceType: ResourceType?,
    @Enumerated(EnumType.STRING)
    val buildingType: BuildingType?,
    @Enumerated(EnumType.STRING)
    val vehicleStat: VehicleStat?,
    val value1: Int,
    val value2: Int?
)
