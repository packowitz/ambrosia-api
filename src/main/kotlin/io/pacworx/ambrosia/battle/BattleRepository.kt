package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import org.springframework.data.jpa.repository.JpaRepository

interface BattleRepository : JpaRepository<Battle, Long> {

    fun findTopByPlayerIdAndStatusIn(playerId: Long, status: List<BattleStatus>): Battle?
}
