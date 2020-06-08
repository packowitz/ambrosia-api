package io.pacworx.ambrosia.battle

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BattleStepRepository : JpaRepository<BattleStep, Long> {
    fun findAllByBattleIdOrderByTurnAscPhaseAscIdAsc(battleId: Long): List<BattleStep>

    fun deleteAllByBattleId(battleId: Long)
}