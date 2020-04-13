package io.pacworx.ambrosia.battle

import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.time.temporal.ChronoUnit

interface BattleRepository : JpaRepository<Battle, Long> {

    fun findTopByPlayerIdAndStatusInAndPreviousBattleIdNull(playerId: Long, status: List<BattleStatus>): Battle?

    fun findTopByPlayerIdAndStatusNotIn(playerId: Long, status: List<BattleStatus>): Battle?

    fun findTop10ByLastActionBeforeAndStatusInAndPreviousBattleIdNull(
        lastAction: Instant = Instant.now().minus(3, ChronoUnit.DAYS),
        states: List<BattleStatus> = listOf(BattleStatus.WON, BattleStatus.LOST)): List<Battle>
}
