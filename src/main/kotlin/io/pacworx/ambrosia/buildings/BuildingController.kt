package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("building")
class BuildingController(private val buildingRepository: BuildingRepository,
                         private val heroRepository: HeroRepository,
                         private val heroService: HeroService,
                         private val propertyService: PropertyService) {

    @PostMapping("academy/hero/{heroId}/level")
    @Transactional
    fun levelHero(@ModelAttribute("player") player: Player,
                  @PathVariable heroId: Long,
                  @RequestBody request: AcademyRequest): PlayerActionResponse {
        val academy = buildingRepository.findByPlayerIdAndType(player.id, BuildingType.ACADEMY)
            ?: throw RuntimeException("You have to find the academy first")
        if (academy.upgradeFinished != null) {
            throw RuntimeException("Academy is busy right now")
        }
        val heroes = heroRepository.findAllByPlayerIdAndIdIn(player.id,
            listOfNotNull(heroId, request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id, request.hero5Id, request.hero6Id)
        )
        val hero = heroes.find { it.id == heroId }
            ?: throw RuntimeException("Unknown hero $heroId for player ${player.id}")
        val deletedHeroIds = heroes.filter { it.id != heroId }.map {
            val gainedXp = propertyService.getHeroMergedXp(it.level)
            heroService.heroGainXp(hero, gainedXp)
            if (hero.heroBase.heroClass == it.heroBase.heroClass) {
                val gainedAsc = propertyService.getHeroMergedAsc(it.heroBase.rarity.stars)
                heroService.heroGainAsc(hero, gainedAsc)
            }
            it.id
        }
        heroRepository.deleteAllByIdIn(deletedHeroIds)
        return PlayerActionResponse(heroes = listOf(heroService.asHeroDto(hero)), heroIdsRemoved = deletedHeroIds)
    }

    @PostMapping("academy/hero/{heroId}/evolve")
    @Transactional
    fun evolveHero(@ModelAttribute("player") player: Player,
                   @PathVariable heroId: Long,
                   @RequestBody request: AcademyRequest): PlayerActionResponse {
        val academy = buildingRepository.findByPlayerIdAndType(player.id, BuildingType.ACADEMY)
            ?: throw RuntimeException("You have to find the academy first")
        if (academy.upgradeFinished != null) {
            throw RuntimeException("Academy is busy right now")
        }
        val heroes = heroRepository.findAllByPlayerIdAndIdIn(player.id,
            listOfNotNull(heroId, request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id, request.hero5Id, request.hero6Id)
        )
        val hero = heroes.find { it.id == heroId }
            ?: throw RuntimeException("Unknown hero $heroId for player ${player.id}")
        val deletedHeroIds = heroes.filter { it.id != heroId }.map {
            if (it.stars < hero.stars) {
                throw RuntimeException("You need heroes of at least same number of stars to evolve a hero")
            }
            if (hero.heroBase.heroClass == it.heroBase.heroClass) {
                val gainedAsc = propertyService.getHeroMergedAsc(it.heroBase.rarity.stars)
                heroService.heroGainAsc(hero, gainedAsc)
            }
            it.id
        }
        heroRepository.deleteAllByIdIn(deletedHeroIds)
        if (!heroService.evolveHero(hero)) {
            throw RuntimeException("Evolving hero failed")
        }
        return PlayerActionResponse(heroes = listOf(heroService.asHeroDto(hero)), heroIdsRemoved = deletedHeroIds)
    }
}

data class AcademyRequest(
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?,
    val hero5Id: Long?,
    val hero6Id: Long?
)