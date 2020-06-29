package io.pacworx.ambrosia.expedition

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class ExpeditionCleanupScheduler(private val playerExpeditionRepository: PlayerExpeditionRepository) {
    private val log = KotlinLogging.logger {}

    @Scheduled(initialDelay = 300000, fixedDelay = 3600000)
    @Transactional
    fun cleanup() {
        playerExpeditionRepository.deleteAllByCompletedIsTrueAndFinishTimestampIsBefore()
    }
}