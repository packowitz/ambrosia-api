package io.pacworx.ambrosia.progress

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Progress(
    @Id
    val playerId: Long,
    val garageSlots: Int = 1,
    val offlineBattleSpeed: Int = 100,
    val maxOfflineBattlesPerMission: Int = 5,
    val builderQueueLength: Int = 1,
    val builderSpeed: Int = 100,
    var barrackSize: Int = 0,
    var maxTrainingLevel: Int = 0,
    var vehicleStorage: Int = 0,
    var vehiclePartStorage: Int = 0
)
