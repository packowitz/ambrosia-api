package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.common.procs
import io.pacworx.ambrosia.hero.Rarity
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import javax.transaction.Transactional
import kotlin.random.Random

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

    private fun randomRarity(): Rarity {
        val random = Random.nextInt(100)
        if (procs(1, random)) { return Rarity.LEGENDARY }
        if (procs(3, random)) { return Rarity.EPIC }
        if (procs(6, random)) { return Rarity.RARE }
        if (procs(15, random)) { return Rarity.UNCOMMON }
        if (procs(25, random)) { return Rarity.COMMON }
        return Rarity.SIMPLE
    }
}