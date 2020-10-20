package io.pacworx.ambrosia.buildings.merchant

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
data class MerchantPlayerItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @JsonIgnore
    val created: LocalDateTime = LocalDateTime.now(),
    val sortOrder: Int,
    val merchantLevel: Int,
    var amountAvailable: Int,
    var sold: Boolean = false,
    @Enumerated(EnumType.STRING)
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val priceType: ResourceType,
    val priceAmount: Int,
    @Enumerated(EnumType.STRING)
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    var resourceType: ResourceType? = null,
    var resourceAmount: Int? = null,
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

    fun getSecondsUntilRefresh(): Long {
        val endOfDay = LocalDate.now().atTime(LocalTime.MAX)
        return LocalDateTime.now().until(endOfDay, ChronoUnit.SECONDS)
    }
}