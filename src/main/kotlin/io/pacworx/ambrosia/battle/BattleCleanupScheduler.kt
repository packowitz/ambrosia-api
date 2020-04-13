package io.pacworx.ambrosia.battle

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class BattleCleanupScheduler(private val battleRepository: BattleRepository,
                             private val battleHeroRepository: BattleHeroRepository) {
    private val log = KotlinLogging.logger {}

    @Scheduled(initialDelay = 60000, fixedDelay = 600000)
    @Transactional
    fun cleanup() {
        val battles = battleRepository.findTop10ByLastActionBeforeAndStatusInAndPreviousBattleIdNull()
        log.info("Deleting ${battles.size} outdated battles")
        battles.forEach {
            var stage = it
            while (stage.nextBattleId != null) {
                stage = battleRepository.getOne(stage.nextBattleId!!)
                battleRepository.delete(stage)
            }
            battleRepository.delete(it)
            it.hero1?.let { battleHeroRepository.delete(it) }
            it.hero2?.let { battleHeroRepository.delete(it) }
            it.hero3?.let { battleHeroRepository.delete(it) }
            it.hero4?.let { battleHeroRepository.delete(it) }
        }
    }
}