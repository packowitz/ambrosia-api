package io.pacworx.ambrosia.progress

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Progress(
    @Id
    val playerId: Long,
    val garageSlots: Int = 1,
    val offlineBattleSpeed: Int = 0
)
