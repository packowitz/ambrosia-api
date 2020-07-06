package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.hero.HeroStat
import io.pacworx.ambrosia.progress.ProgressStat
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.vehicle.VehicleStat
import javax.persistence.*

@Entity
data class DynamicProperty(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val category: PropertyCategory,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val type: PropertyType,
    var version: Int,
    val level: Int?,
    @Enumerated(EnumType.STRING)
    val stat: HeroStat?,
    @Enumerated(EnumType.STRING)
    val progressStat: ProgressStat?,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val resourceType: ResourceType?,
    @Enumerated(EnumType.STRING)
    val vehicleStat: VehicleStat?,
    val value1: Int,
    val value2: Int?
) {
    constructor(copy: DynamicProperty, version: Int): this(
        category = copy.category,
        type = copy.type,
        version = version,
        level = copy.level,
        stat = copy.stat,
        progressStat = copy.progressStat,
        resourceType = copy.resourceType,
        vehicleStat = copy.vehicleStat,
        value1 = copy.value1,
        value2 = copy.value2
    )
}
