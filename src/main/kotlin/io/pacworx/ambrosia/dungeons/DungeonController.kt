package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.PlayerRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/dungeon")
class DungeonController(private val dungeonRepository: DungeonRepository,
                        private val playerRepository: PlayerRepository,
                        private val heroService: HeroService) {

    @GetMapping
    fun getAllDungeons(): List<Dungeon> = dungeonRepository.findAll()

    @GetMapping("{id}")
    fun getDungeon(@PathVariable id: Long): DungeonResolved {
        val dungeon = dungeonRepository.getOne(id)
        return asDungeonResolved(dungeon)
    }

    @PostMapping("new")
    @Transactional
    fun createDungeopn(@RequestBody dungeon: Dungeon): Dungeon {
        return dungeonRepository.save(dungeon)
    }

    @PutMapping("{id}")
    @Transactional
    fun updateDungeon(@PathVariable id: Long, @RequestBody @Valid dungeon: Dungeon): DungeonResolved {
        return asDungeonResolved(dungeonRepository.save(dungeon))
    }

    private fun asDungeonResolved(dungeon: Dungeon): DungeonResolved {
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
