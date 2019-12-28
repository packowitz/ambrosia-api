package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import org.springframework.data.jpa.repository.JpaRepository

interface BattleRepository : JpaRepository<Battle, Long> {

    fun findTopByPlayerIdAndStatusInAndPreviousBattleIdNull(playerId: Long, status: List<BattleStatus>): Battle?

    fun findTopByPlayerIdAndStatusNotIn(playerId: Long, status: List<BattleStatus>): Battle?
}
