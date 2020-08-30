package io.pacworx.ambrosia.inbox

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class InboxCleanupScheduler(private val inboxMessageRepository: InboxMessageRepository) {
    private val log = KotlinLogging.logger {}

    @Scheduled(initialDelay = 30000, fixedDelay = 300000)
    @Transactional
    fun cleanup() {
        val messages = inboxMessageRepository.findAllByValidTimestampIsBefore()
        log.info("Deleting ${messages.size} unclaimed inbox messages")
        messages.forEach {
            inboxMessageRepository.delete(it)
        }
    }
}