package io.pacworx.ambrosia.expedition

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit

@Repository
interface PlayerExpeditionRepository : JpaRepository<PlayerExpedition, Long> {

    fun findAllByPlayerIdOrderByStartTimestamp(playerId: Long): List<PlayerExpedition>

    fun findAllByPlayerIdAndExpeditionIdIn(playerId: Long, expeditionIds: List<Long>): List<PlayerExpedition>

    fun deleteAllByCompletedIsTrueAndFinishTimestampIsBefore(
        finishTimestamp: Instant = Instant.now().minus(5, ChronoUnit.HOURS)
    )
}