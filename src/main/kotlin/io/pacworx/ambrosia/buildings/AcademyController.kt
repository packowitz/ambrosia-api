package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.battle.BattleRepository
import io.pacworx.ambrosia.battle.BattleService
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.HeroBusyException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.properties.PropertyService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("academy")
class AcademyController(private val heroRepository: HeroRepository,
                        private val heroService: HeroService,
                        private val propertyService: PropertyService,
                        private val progressRepository: ProgressRepository,
                        private val battleRepository: BattleRepository,
                        private val battleService: BattleService,
                        private val auditLogService: AuditLogService) {

    @PostMapping("hero/{heroId}/level")
    @Transactional
    fun levelHero(@ModelAttribute("player") player: Player,
                  @PathVariable heroId: Long,
                  @RequestBody request: AcademyRequest): PlayerActionResponse {
        val heroes = heroRepository.findAllByPlayerIdAndIdIn(player.id,
            listOfNotNull(heroId, request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id, request.hero5Id, request.hero6Id)
        )
        heroes.find { !it.isAvailable() }?.let { throw HeroBusyException(player, it) }
        val hero = heroes.find { it.id == heroId }
            ?: throw UnauthorizedException(player, "Given hero cannot get upgraded by you")
        val progress = progressRepository.getOne(player.id)
        if (hero.level > progress.maxTrainingLevel) {
            throw GeneralException(player, "Cannot train hero", "Hero's level too high to get trained in the academy. Level up your Academy.")
        }
        val heroLevelBefore = hero.level
        val heroXpBefore = hero.xp
        val updatedGear = mutableListOf<Gear>()
        val deletedHeroes = heroes.filter { it.id != heroId }.map { fodder ->
            val gainedXp = propertyService.getHeroMergedXp(fodder.level)
            heroService.heroGainXp(hero, gainedXp)
            if (hero.heroBase.heroClass == fodder.heroBase.heroClass) {
                val gainedAsc = propertyService.getHeroMergedAsc(fodder.heroBase.rarity.stars)
                heroService.heroGainAsc(hero, gainedAsc)
            }
            battleRepository.findAllByContainingHero(fodder.id).forEach { battleId ->
                battleService.deleteBattle(battleRepository.getOne(battleId))
            }
            updatedGear.addAll(fodder.unequipAll())
            fodder
        }
        auditLogService.log(player, "Level up hero ${hero.heroBase.name} #${hero.id} in the academy from lvl $heroLevelBefore $heroXpBefore xp to lvl ${hero.level} ${hero.xp} xp " +
                "feeding heroes ${deletedHeroes.joinToString { "${it.heroBase.name} #${it.id} lvl ${it.level}" }} " +
                "unequipped gear from fodder: ${updatedGear.joinToString { "${it.rarity.stars}* ${it.set.name} ${it.type.name} #${it.id}" }}"
        )
        heroRepository.deleteAllByIdIn(deletedHeroes.map { it.id })
        return PlayerActionResponse(
            heroes = listOf(heroService.asHeroDto(hero)),
            gears = updatedGear,
            heroIdsRemoved = deletedHeroes.map { it.id })
    }

    @PostMapping("hero/{heroId}/evolve")
    @Transactional
    fun evolveHero(@ModelAttribute("player") player: Player,
                   @PathVariable heroId: Long,
                   @RequestBody request: AcademyRequest): PlayerActionResponse {
        val heroes = heroRepository.findAllByPlayerIdAndIdIn(player.id,
            listOfNotNull(heroId, request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id, request.hero5Id, request.hero6Id)
        )
        heroes.find { !it.isAvailable() }?.let { throw HeroBusyException(player, it) }

        val hero = heroes.find { it.id == heroId }
            ?: throw UnauthorizedException(player, "Given hero cannot get evolved by you")

        val progress = progressRepository.getOne(player.id)
        if (hero.level > progress.maxTrainingLevel) {
            throw GeneralException(player, "Cannot evolve hero", "Hero's level too high to get evolved. Level up your Academy.")
        }
        val updatedGear = mutableListOf<Gear>()
        val deletedHeroes = heroes.filter { it.id != heroId }.map { fodder ->
            if (fodder.stars < hero.stars) {
                throw GeneralException(player, "Cannot evolve hero", "You need heroes of at least same number of stars to evolve a hero")
            }
            if (hero.heroBase.heroClass == fodder.heroBase.heroClass) {
                val gainedAsc = propertyService.getHeroMergedAsc(fodder.heroBase.rarity.stars)
                heroService.heroGainAsc(hero, gainedAsc)
            }
            battleRepository.findAllByContainingHero(fodder.id).forEach { battleId ->
                battleService.deleteBattle(battleRepository.getOne(battleId))
            }
            updatedGear.addAll(fodder.unequipAll())
            fodder
        }
        heroRepository.deleteAllByIdIn(deletedHeroes.map { it.id })
        if (!heroService.evolveHero(hero)) {
            throw GeneralException(player, "Cannot evolve hero", "Evolving hero failed")
        }
        auditLogService.log(player, "Evolve hero ${hero.heroBase.name} #${hero.id} to ${hero.stars}* lvl ${hero.level} " +
                "feeding heroes ${deletedHeroes.joinToString { "${it.heroBase.name} #${it.id} lvl ${it.level}" }} " +
                "unequipped gear from fodder: ${updatedGear.joinToString { "${it.rarity.stars}* ${it.set.name} ${it.type.name} #${it.id}" }}"
        )
        return PlayerActionResponse(
            heroes = listOf(heroService.asHeroDto(hero)),
            gears = updatedGear,
            heroIdsRemoved = deletedHeroes.map { it.id }
        )
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
