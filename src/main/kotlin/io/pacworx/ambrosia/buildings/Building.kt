package io.pacworx.ambrosia.buildings

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Building(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING) val type: BuildingType,
    var level: Int = 1,
    var upgradeTriggered: Boolean = false,
    var upgradeStarted: LocalDateTime? = null,
    var upgradeFinished: LocalDateTime? = null,
    var wipStarted: LocalDateTime? = null,
    var wipFinished: LocalDateTime? = null,
    var wipRef: Long? = null
)