package io.pacworx.ambrosia.buildings

import javax.persistence.*

@Entity
data class Building(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING) val type: BuildingType,
    var level: Int = 1,
    var upgradeTriggered: Boolean = false
)