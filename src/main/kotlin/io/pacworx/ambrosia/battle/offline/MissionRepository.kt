package io.pacworx.ambrosia.battle.offline

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MissionRepository : JpaRepository<Mission, Long> {
    fun findAllByPlayerId(playerId: Long): List<Mission>
}
