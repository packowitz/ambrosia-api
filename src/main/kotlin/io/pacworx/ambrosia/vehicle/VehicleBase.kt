package io.pacworx.ambrosia.vehicle

import javax.persistence.*

@Entity
data class VehicleBase(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val avatar: String = "default",
    val maxLevel: Int = 6,
    @Enumerated(EnumType.STRING) val engineQuality: PartQuality = PartQuality.BASIC,
    @Enumerated(EnumType.STRING) val frameQuality: PartQuality = PartQuality.BASIC,
    @Enumerated(EnumType.STRING) val computerQuality: PartQuality = PartQuality.BASIC,
    @Enumerated(EnumType.STRING) val specialPart1: PartType?,
    @Enumerated(EnumType.STRING) var specialPart1Quality: PartQuality?,
    @Enumerated(EnumType.STRING) val specialPart2: PartType?,
    @Enumerated(EnumType.STRING) var specialPart2Quality: PartQuality?,
    @Enumerated(EnumType.STRING) val specialPart3: PartType?,
    @Enumerated(EnumType.STRING) var specialPart3Quality: PartQuality?
)
