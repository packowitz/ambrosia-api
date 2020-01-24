package io.pacworx.ambrosia.buildings

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Building(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING) val type: BuildingType,
    val level: Int = 1,
    var upgradeStarted: LocalDateTime? = null,
    var upgradeFinished: LocalDateTime? = null
)