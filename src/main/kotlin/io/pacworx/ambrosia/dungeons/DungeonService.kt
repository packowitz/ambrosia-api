package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.stereotype.Service

@Service
class DungeonService(private val playerRepository: PlayerRepository,
                     private val heroService: HeroService) {

    fun asDungeonResolved(dungeon: Dungeon): DungeonResolved {
        return DungeonResolved(
            dungeon.id,
            dungeon.name,
            playerRepository.findByServiceAccountIsTrueAndId(dungeon.serviceAccountId),
            dungeon.stages.map {stage ->
                DungeonStageResolved(
                    stage.id,
                    stage.stage,
                    stage.hero1Id?.let { heroService.getHeroDto(it) },
                    stage.hero2Id?.let { heroService.getHeroDto(it) },
                    stage.hero3Id?.let { heroService.getHeroDto(it) },
                    stage.hero4Id?.let { heroService.getHeroDto(it) }
                ) }
        )
    }

    data class DungeonResolved(
        val id: Long,
        val name: String,
        val serviceAccount: Player,
        val stages: List<DungeonStageResolved>
    )

    data class DungeonStageResolved(
        val id: Long,
        val stage: Int,
        val hero1: HeroDto?,
        val hero2: HeroDto?,
        val hero3: HeroDto?,
        val hero4: HeroDto?
    )
}