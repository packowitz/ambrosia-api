package io.pacworx.ambrosia.achievements

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerTaskRepository : JpaRepository<PlayerTask, Long> {

    fun findByPlayerId(playerId: Long): List<PlayerTask>

    fun findByPlayerIdAndTaskClusterId(playerId: Long, clusterId: Long): PlayerTask?

}