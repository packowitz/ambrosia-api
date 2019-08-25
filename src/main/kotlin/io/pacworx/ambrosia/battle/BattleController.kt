package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle")
class BattleController(private val heroService: HeroService,
                       private val battleRepository: BattleRepository,
                       private val battleHeroRepository: BattleHeroRepository) {

    @PostMapping
    fun startPvpBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartBattleRequest): Battle {
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            request.oppHero1Id, request.oppHero1Id, request.oppHero1Id, request.oppHero4Id))
        val battle = Battle(
            type = BattleType.DUELL,
            playerId = player.id,
            opponentId = request.oppPlayerId,
            hero1 = request.hero1Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            hero2 = request.hero2Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            hero3 = request.hero3Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            hero4 = request.hero4Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            oppHero1 = request.oppHero1Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            oppHero2 = request.oppHero2Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            oppHero3 = request.oppHero3Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } },
            oppHero4 = request.oppHero4Id?.let { heroId -> heroes.find { it.id == heroId }?.let { BattleHero(heroService.asHeroDto(it), it.heroBase) } }
        )
        return battleRepository.save(battle)
    }
}

data class StartBattleRequest(
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?,
    val oppPlayerId: Long,
    val oppHero1Id: Long?,
    val oppHero2Id: Long?,
    val oppHero3Id: Long?,
    val oppHero4Id: Long?
)
