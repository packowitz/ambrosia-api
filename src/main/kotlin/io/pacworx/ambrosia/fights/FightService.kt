package io.pacworx.ambrosia.fights

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.stereotype.Service

@Service
class FightService(private val playerRepository: PlayerRepository,
                   private val heroService: HeroService) {

    fun asFightResolved(fight: Fight): FightResolved {
        return FightResolved(
            fight.id,
            fight.name,
            playerRepository.findByServiceAccountIsTrueAndId(fight.serviceAccountId),
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
        val serviceAccount: Player,
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
