package io.pacworx.ambrosia.fights

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.fights.environment.FightEnvironment
import io.pacworx.ambrosia.fights.stageconfig.FightStageConfig
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.loot.LootBox
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.resources.ResourceType
import org.springframework.stereotype.Service

@Service
class FightService(private val playerRepository: PlayerRepository,
                   private val heroService: HeroService) {

    fun asFightResolved(fight: Fight): FightResolved {
        return FightResolved(
            fight.id,
            fight.name,
            fight.description,
            playerRepository.findByServiceAccountIsTrueAndId(fight.serviceAccountId),
            fight.resourceType,
            fight.lootBox,
            fight.costs,
            fight.xp,
            fight.level,
            fight.ascPoints,
            fight.travelDuration,
            fight.timePerTurn,
            fight.maxTurnsPerStage,
            fight.stageConfig,
            fight.environment,
            fight.stages.map { stage ->
                FightStageResolved(
                    stage.id,
                    stage.stage,
                    stage.hero1Id?.let { heroService.getHeroDto(it) },
                    stage.hero2Id?.let { heroService.getHeroDto(it) },
                    stage.hero3Id?.let { heroService.getHeroDto(it) },
                    stage.hero4Id?.let { heroService.getHeroDto(it) }
                ) }
        )
    }

    data class FightResolved(
        val id: Long,
        val name: String,
        val description: String,
        val serviceAccount: Player,
        @JsonFormat(shape = JsonFormat.Shape.STRING) val resourceType: ResourceType,
        val lootBox: LootBox,
        val costs: Int,
        val xp: Int,
        val level: Int,
        val ascPoints: Int,
        val travelDuration: Int,
        val timePerTurn: Int,
        val maxTurnsPerStage: Int,
        val stageConfig: FightStageConfig,
        val environment: FightEnvironment,
        val stages: List<FightStageResolved>
    )

    data class FightStageResolved(
        val id: Long,
        val stage: Int,
        val hero1: HeroDto?,
        val hero2: HeroDto?,
        val hero3: HeroDto?,
        val hero4: HeroDto?
    )
}
