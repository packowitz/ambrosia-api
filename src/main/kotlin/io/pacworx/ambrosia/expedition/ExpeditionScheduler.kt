package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.common.randomRarity
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.transaction.Transactional

@Component
class ExpeditionScheduler(
    private val expeditionRepository: ExpeditionRepository,
    private val expeditionBaseRepository: ExpeditionBaseRepository
) {

    private val log = KotlinLogging.logger {}

    @Scheduled(cron = "0 0 */1 * * *")
    @Transactional
    fun rotateExpeditions() {
        val rarity = randomRarity()
        log.info("Expedition generation started to create $rarity expeditions")
        val now = Instant.now()
        val availableUntil = now.plus(5, ChronoUnit.HOURS)
        (1..6).forEach { level ->
            val activeExpeditions = expeditionRepository.findAllByExpeditionBase_LevelAndActiveIsTrue(level)
            activeExpeditions
                .filter { it.created.plus(235, ChronoUnit.MINUTES).isBefore(now) }
                .forEach { it.active = false }
            val expBase = expeditionBaseRepository.findAllByLevelAndRarity(level, rarity).random()
            if (activeExpeditions.count { it.active } < 4) {
                expeditionRepository.save(Expedition(
                    expeditionBase = expBase,
                    active = true,
                    created = now,
                    availableUntil = availableUntil
                ))
            }
        }
    }
}