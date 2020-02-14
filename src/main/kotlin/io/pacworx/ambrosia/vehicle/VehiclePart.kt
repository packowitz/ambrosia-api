package io.pacworx.ambrosia.vehicle

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class VehiclePart(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    val equippedTo: Long?,
    val type: PartType,
    val level: Int = 1
)
