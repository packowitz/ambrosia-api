package io.pacworx.ambrosia.vehicle

import javax.persistence.*

@Entity
data class VehiclePart(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    var equippedTo: Long? = null,
    val type: PartType,
    @Enumerated(EnumType.STRING)
    val quality: PartQuality,
    var level: Int = 1,
    var upgradeTriggered: Boolean = false
)
