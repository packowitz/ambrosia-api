package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkill
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class BattleService(private val heroService: HeroService,
                    private val battleRepository: BattleRepository,
                    private val skillService: SkillService,
                    private val aiService: AiService,
                    private val propertyService: PropertyService) {

    companion object {
        private const val SPEEDBAR_MAX: Int = 10000
        private const val SPEEDBAR_TURN: Int = 100
    }

    fun initBattle(player: Player, request: StartBattleRequest): Battle {
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            request.oppHero1Id, request.oppHero2Id, request.oppHero3Id, request.oppHero4Id))
        val battle = battleRepository.save(Battle(
            type = BattleType.DUELL,
            playerId = player.id,
            opponentId = request.oppPlayerId,
            hero1 = request.hero1Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(player.id, heroService.asHeroDto(it), it.heroBase, HeroPosition.HERO1)
            } },
            hero2 = request.hero2Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(player.id, heroService.asHeroDto(it), it.heroBase, HeroPosition.HERO2)
            } },
            hero3 = request.hero3Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(player.id, heroService.asHeroDto(it), it.heroBase, HeroPosition.HERO3)
            } },
            hero4 = request.hero4Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(player.id, heroService.asHeroDto(it), it.heroBase, HeroPosition.HERO4)
            } },
            oppHero1 = request.oppHero1Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(request.oppPlayerId, heroService.asHeroDto(it), it.heroBase, HeroPosition.OPP1)
            } },
            oppHero2 = request.oppHero2Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(request.oppPlayerId, heroService.asHeroDto(it), it.heroBase, HeroPosition.OPP2)
            } },
            oppHero3 = request.oppHero3Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(request.oppPlayerId, heroService.asHeroDto(it), it.heroBase, HeroPosition.OPP3)
            } },
            oppHero4 = request.oppHero4Id?.let { heroId -> heroes.find { it.id == heroId }?.let {
                BattleHero(request.oppPlayerId, heroService.asHeroDto(it), it.heroBase, HeroPosition.OPP4)
            } }
        ))
        battle.allHeroes().shuffled().forEachIndexed { idx, hero ->
            hero.priority = idx
        }

        battle.applyBonuses(propertyService)
        nextTurn(battle)

        return battleRepository.save(battle)
    }

    @Transactional
    fun takeTurn(battle: Battle, activeHero: BattleHero, skill: HeroSkill, target: BattleHero): Battle {
        skillService.useSkill(battle, activeHero, skill, target)
        if (!battle.hasEnded()) {
            nextTurn(battle)
        }
        return battleRepository.save(battle)
    }

    @Transactional
    fun takeAutoTurn(battle: Battle, activeHero: BattleHero): Battle {
        aiService.doAction(battle, activeHero)
        if (!battle.hasEnded()) {
            nextTurn(battle)
        }
        return battleRepository.save(battle)
    }

    private fun nextTurn(battle: Battle) {
        val activeHero = nextActiveHero(battle)
        if (activeHero != null) {

            battle.setActiveHero(activeHero)
            battle.applyBonuses(propertyService)
            activeHero.initTurn(skillService, battle)
            battle.checkStatus()

            if (battle.hasEnded()) {
                return
            }
            if (activeHero.status == HeroStatus.DEAD) {
                nextTurn(battle)
            }
            if (battle.status == BattleStatus.PLAYER_TURN) {
                // awaiting player input
                return
            } else if (battle.status == BattleStatus.OPP_TURN) {
                aiService.doAction(battle, activeHero)
            }
            if (battle.hasEnded()) {
                return
            }
        } else {
            battle.allHeroesAlive().forEach { it.currentSpeedBar += SPEEDBAR_TURN + it.speedBonus }
        }
        nextTurn(battle)
    }

    private fun nextActiveHero(battle: Battle): BattleHero? {
        return battle.allHeroesAlive()
            .filter { it.currentSpeedBar >= SPEEDBAR_MAX }
            .takeIf { it.isNotEmpty() }
            ?.maxWith(Comparator<BattleHero> { hero1, hero2 ->
                when {
                    hero1.currentSpeedBar > hero2.currentSpeedBar -> 1
                    hero1.currentSpeedBar < hero2.currentSpeedBar -> -1
                    else -> when {
                        hero1.priority > hero2.priority -> 1
                        hero1.priority < hero2.priority -> -1
                        else -> 0
                    }
                }

            })
    }


}
