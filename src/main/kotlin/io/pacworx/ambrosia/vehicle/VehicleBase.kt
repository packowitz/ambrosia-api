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
    val avatar: String,
    val maxLevel: Int,
    val engineQuality: PartQuality,
    val frameQuality: PartQuality,
    val computerQuality: PartQuality,
    val specialPart1Quality: PartQuality?,
    val specialPart2Quality: PartQuality?,
    val specialPart3Quality: PartQuality?
)
