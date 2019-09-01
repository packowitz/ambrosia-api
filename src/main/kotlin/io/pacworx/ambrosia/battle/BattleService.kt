package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class BattleService(private val heroService: HeroService,
                    private val battleRepository: BattleRepository,
                    private val battleHeroRepository: BattleHeroRepository,
                    private val aiService: AiService) {

    private val SPEEDBAR_MAX: Int = 10000
    private val SPEEDBAR_TURN: Int = 100

    fun initBattle(player: Player, request: StartBattleRequest): Battle {
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            request.oppHero1Id, request.oppHero1Id, request.oppHero1Id, request.oppHero4Id))
        val battle = battleRepository.save(Battle(
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
        ))

        nextTurn(battle)

        return battleRepository.save(battle)
    }

    private fun nextTurn(battle: Battle): Battle {
        val activeHero = nextActiveHero(battle)
        if (activeHero != null) {
            battle.setActiveHero(activeHero)
            if(battle.status == BattleStatus.PLAYER_TURN) {
                // awaiting player input
                return battle
            } else {
                aiService.doAction(battle, activeHero)
            }
            battle.lastAction = Instant.now()
            battle.turnsDone ++
            if (battle.status == BattleStatus.LOST || battle.status == BattleStatus.WON) {
                return battle
            }
        } else {
            battle.allHeroesAlive().forEach { it.currentSpeedBar += SPEEDBAR_TURN + it.speedBonus }
        }
        return nextTurn(battle)
    }

    private fun nextActiveHero(battle: Battle): BattleHero? {
        return battle.allHeroesAlive()
            .filter { it.currentSpeedBar >= SPEEDBAR_MAX }
            .takeIf { it.isNotEmpty() }
            ?.random()
    }


}