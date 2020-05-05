package io.pacworx.ambrosia.battle

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class BattleCleanupScheduler(private val battleRepository: BattleRepository,
                             private val battleService: BattleService) {
    private val log = KotlinLogging.logger {}

    @Scheduled(initialDelay = 60000, fixedDelay = 600000)
    @Transactional
    fun cleanup() {
        val battles = battleRepository.findTop10ByLastActionBeforeAndStatusInAndPreviousBattleIdNull()
        log.info("Deleting ${battles.size} outdated battles")
        battles.forEach {
            battleService.deleteBattle(it)
        }
    }
}