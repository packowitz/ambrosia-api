package io.pacworx.ambrosia.vehicle

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class VehicleBase(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val avatar: String = "default",
    val maxLevel: Int = 6,
    val engineQuality: PartQuality = PartQuality.BASIC,
    val frameQuality: PartQuality = PartQuality.BASIC,
    val computerQuality: PartQuality = PartQuality.BASIC,
    val specialPart1: PartType?,
    var specialPart1Quality: PartQuality?,
    val specialPart2: PartType?,
    var specialPart2Quality: PartQuality?,
    val specialPart3: PartType?,
    var specialPart3Quality: PartQuality?
)
