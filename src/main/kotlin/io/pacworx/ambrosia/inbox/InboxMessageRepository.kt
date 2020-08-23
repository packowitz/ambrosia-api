package io.pacworx.ambrosia.inbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface InboxMessageRepository: JpaRepository<InboxMessage, Long> {
    fun findAllByPlayerIdOrderByValidTimestamp(playerId: Long): List<InboxMessage>

    fun findAllByPlayerIdAndSendTimestampIsAfter(playerId: Long, timestamp: LocalDateTime): List<InboxMessage>
}