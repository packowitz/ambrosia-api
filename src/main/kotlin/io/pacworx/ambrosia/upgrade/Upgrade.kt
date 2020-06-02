package io.pacworx.ambrosia.upgrade

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.resources.ResourceType
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Upgrade(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    var position: Int,
    @JsonIgnore var startTimestamp: Instant,
    @JsonIgnore var finishTimestamp: Instant,
    @JsonIgnore var resources: String = "",
    val buildingType: BuildingType? = null,
    val vehicleId: Long? = null,
    val vehiclePartId: Long? = null,
    val jewelType: JewelType? = null,
    val jewelLevel: Int? = null,
    val gearModification: Modification? = null,
    val gearId: Long? = null,
    var secondsSpend: Int = 0
) {
    fun isFinished(): Boolean = finishTimestamp.isBefore(Instant.now())

    fun isInProgress(): Boolean = if (isFinished()) { false } else { startTimestamp.isBefore(Instant.now()) }

    fun getDuration(): Long = startTimestamp.until(finishTimestamp, ChronoUnit.SECONDS)

    fun getSecondsUntilDone(): Long =
        if (isFinished()) { 0 } else { Instant.now().until(finishTimestamp, ChronoUnit.SECONDS) + 2 }

    fun setResources(costs: List<Cost>) {
        this.resources = costs.map { "${it.type}:${it.amount}" }.joinToString(separator = ";")
    }

    @JsonIgnore
    fun getResourcesAsCosts(): List<Cost> {
        return this.resources.split(";").map {
            val splitted = it.split(":")
            Cost(splitted[1].toInt(), ResourceType.valueOf(splitted[0]))
        }
    }
}
