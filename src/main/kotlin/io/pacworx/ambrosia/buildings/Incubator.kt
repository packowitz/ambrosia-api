package io.pacworx.ambrosia.buildings

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.speedup.Speedup
import io.pacworx.ambrosia.upgrade.Cost
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
data class Incubator(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING) val type: GenomeType,
    @JsonIgnore var startTimestamp: Instant,
    @JsonIgnore var finishTimestamp: Instant,
    @JsonIgnore var resources: String = ""
) {
    @Transient
    var speedup: Speedup? = null

    fun isFinished(): Boolean = finishTimestamp.isBefore(Instant.now())

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