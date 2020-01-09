package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons.DungeonRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.TeamType
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.SimplePlayerMapTile
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class BattleService(private val playerRepository: PlayerRepository,
                    private val heroService: HeroService,
                    private val battleRepository: BattleRepository,
                    private val skillService: SkillService,
                    private val aiService: AiService,
                    private val propertyService: PropertyService,
                    private val dungeonRepository: DungeonRepository,
                    private val teamRepository: TeamRepository) {

    companion object {
        const val SPEEDBAR_MAX: Int = 10000
        private const val SPEEDBAR_TURN: Int = 100
    }

    @Transactional
    fun initDuell(player: Player, request: StartDuellRequest): Battle {
        val opponent = playerRepository.getOne(request.oppPlayerId)
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            request.oppHero1Id, request.oppHero2Id, request.oppHero3Id, request.oppHero4Id))
        val battle = battleRepository.save(Battle(
            type = BattleType.DUELL,
            playerId = player.id,
            playerName = player.name,
            opponentId = request.oppPlayerId,
            opponentName = opponent.name,
            hero1 = asBattleHero(player.id, request.hero1Id, HeroPosition.HERO1, heroes),
            hero2 = asBattleHero(player.id, request.hero2Id, HeroPosition.HERO2, heroes),
            hero3 = asBattleHero(player.id, request.hero3Id, HeroPosition.HERO3, heroes),
            hero4 = asBattleHero(player.id, request.hero4Id, HeroPosition.HERO4, heroes),
            oppHero1 = asBattleHero(request.oppPlayerId, request.oppHero1Id, HeroPosition.OPP1, heroes),
            oppHero2 = asBattleHero(request.oppPlayerId, request.oppHero2Id, HeroPosition.OPP2, heroes),
            oppHero3 = asBattleHero(request.oppPlayerId, request.oppHero3Id, HeroPosition.OPP3, heroes),
            oppHero4 = asBattleHero(request.oppPlayerId, request.oppHero4Id, HeroPosition.OPP4, heroes)
        ))
        battle.allHeroes().shuffled().forEachIndexed { idx, hero ->
            hero.priority = idx
        }
        return startBattle(battle)
    }

    @Transactional
    fun initCampaign(player: Player, mapTile: SimplePlayerMapTile, request: StartBattleRequest): Battle {
        val dungeon = dungeonRepository.getOne(mapTile.fightId!!)
        val team = teamRepository.findByPlayerIdAndType(player.id, TeamType.CAMPAIGN) ?: teamRepository.save(Team ( playerId = player.id, type = TeamType.CAMPAIGN))
        team.apply {
            hero1Id = request.hero1Id
            hero2Id = request.hero2Id
            hero3Id = request.hero3Id
            hero4Id = request.hero4Id
        }
        val dungeonStage = dungeon.stages.find { it.stage == 1 } ?: throw RuntimeException("Dungeon " + dungeon.name + " has no stages defined.")
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            dungeonStage.hero1Id, dungeonStage.hero2Id, dungeonStage.hero3Id, dungeonStage.hero4Id))
        val battle = battleRepository.save(Battle(
            type = BattleType.CAMPAIGN,
            dungeon = dungeon,
            dungeonStage = dungeonStage.stage,
            mapId = mapTile.mapId,
            mapPosX = mapTile.posX,
            mapPosY = mapTile.posY,
            playerId = player.id,
            playerName = player.name,
            opponentName = dungeon.name + " Stage-" + dungeonStage.stage,
            hero1 = asBattleHero(player.id, request.hero1Id, HeroPosition.HERO1, heroes),
            hero2 = asBattleHero(player.id, request.hero2Id, HeroPosition.HERO2, heroes),
            hero3 = asBattleHero(player.id, request.hero3Id, HeroPosition.HERO3, heroes),
            hero4 = asBattleHero(player.id, request.hero4Id, HeroPosition.HERO4, heroes),
            oppHero1 = asBattleHero(heroId = dungeonStage.hero1Id, position = HeroPosition.OPP1, heroes = heroes),
            oppHero2 = asBattleHero(heroId = dungeonStage.hero2Id, position = HeroPosition.OPP2, heroes = heroes),
            oppHero3 = asBattleHero(heroId = dungeonStage.hero3Id, position = HeroPosition.OPP3, heroes = heroes),
            oppHero4 = asBattleHero(heroId = dungeonStage.hero4Id, position = HeroPosition.OPP4, heroes = heroes)
        ))
        battle.allHeroes().shuffled().forEachIndexed { idx, hero ->
            hero.priority = idx
        }
        return startBattle(battle)
    }

    @Transactional
    fun startBattle(battle: Battle): Battle {
        battle.applyBonuses(propertyService)
        nextTurn(battle)
        return battle
    }

    private fun initNextStage(battle: Battle) {
        val dungeon = dungeonRepository.getOne(battle.dungeon!!.id)
        val nextStage = dungeon.stages.find { it.stage > battle.dungeonStage!! } ?: throw RuntimeException("Dungeon " + dungeon.name + " has no next stage defined.")
        val heroes = heroService.loadHeroes(listOfNotNull(
            nextStage.hero1Id, nextStage.hero2Id, nextStage.hero3Id, nextStage.hero4Id))
        val nextBattle = battleRepository.save(Battle(
            type = BattleType.CAMPAIGN,
            previousBattleId = battle.id,
            dungeon = dungeon,
            dungeonStage = nextStage.stage,
            mapId = battle.mapId,
            mapPosX = battle.mapPosX,
            mapPosY = battle.mapPosY,
            playerId = battle.playerId,
            playerName = battle.playerName,
            opponentName = dungeon.name + " Stage-" + nextStage.stage,
            hero1 = battle.hero1,
            hero2 = battle.hero2,
            hero3 = battle.hero3,
            hero4 = battle.hero4,
            oppHero1 = asBattleHero(heroId = nextStage.hero1Id, position = HeroPosition.OPP1, heroes = heroes),
            oppHero2 = asBattleHero(heroId = nextStage.hero2Id, position = HeroPosition.OPP2, heroes = heroes),
            oppHero3 = asBattleHero(heroId = nextStage.hero3Id, position = HeroPosition.OPP3, heroes = heroes),
            oppHero4 = asBattleHero(heroId = nextStage.hero4Id, position = HeroPosition.OPP4, heroes = heroes)
        ))
        battle.nextBattleId = nextBattle.id
        nextBattle.allHeroes().shuffled().forEachIndexed { idx, hero ->
            hero.priority = idx
        }
    }

    @Transactional
    fun takeTurn(battle: Battle, activeHero: BattleHero, skill: HeroSkill, target: BattleHero): Battle {
        skillService.useSkill(battle, activeHero, skill, target)
        if (!battleEnded(battle)) {
            nextTurn(battle)
        }
        return battleRepository.save(battle)
    }

    @Transactional
    fun takeAutoTurn(battle: Battle, activeHero: BattleHero): Battle {
        aiService.doAction(battle, activeHero)
        if (!battleEnded(battle)) {
            nextTurn(battle)
        }
        return battleRepository.save(battle)
    }

    private fun asBattleHero(playerId: Long? = null, heroId: Long?, position: HeroPosition, heroes: List<Hero>): BattleHero? =
        heroId?.let { heroId -> heroes.find { it.id == heroId }?.let {
            BattleHero(playerId, heroService.asHeroDto(it), it.heroBase, position)
        }}

    private fun nextTurn(battle: Battle) {
        val activeHero = nextActiveHero(battle)
        if (activeHero != null) {

            battle.setActiveHero(activeHero)
            battle.applyBonuses(propertyService)
            activeHero.initTurn(propertyService, skillService, battle)
            battle.checkStatus()

            if (battleEnded(battle)) {
                return
            }
            if (activeHero.status == HeroStatus.DEAD) {
                nextTurn(battle)
            } else {
                if (activeHero.status == HeroStatus.CONFUSED && battle.allAlliedHeroesAlive(activeHero).filter { it.position != activeHero.position }.isEmpty()) {
                    activeHero.status = HeroStatus.STUNNED
                }
                if (activeHero.status == HeroStatus.STUNNED) {
                    battle.steps.add(BattleStep(
                            turn = battle.turnsDone,
                            phase = BattleStepPhase.MAIN,
                            actingHero = activeHero.position,
                            actingHeroName = activeHero.heroBase.name,
                            target = HeroPosition.NONE,
                            targetName = "STUNNED",
                            heroStates = battle.getBattleStepHeroStates()
                    ))
                    activeHero.afterTurn(battle, propertyService)
                    nextTurn(battle)
                } else if (activeHero.status == HeroStatus.CONFUSED) {
                    val target = battle.allAlliedHeroesAlive(activeHero).filter { it.position != activeHero.position }.random()
                    val skill = activeHero.heroBase.skills.find { it.number == 1 }!!
                    skillService.useSkill(battle, activeHero, skill, target)
                } else {
                    if (battle.status == BattleStatus.PLAYER_TURN) {
                        // awaiting player input
                        return
                    } else if (battle.status == BattleStatus.OPP_TURN) {
                        aiService.doAction(battle, activeHero)
                    }
                }
            }
            if (battleEnded(battle)) {
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
            ?.maxWith(Comparator { hero1, hero2 ->
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

    private fun battleEnded(battle: Battle): Boolean {
        if (battle.status == BattleStatus.STAGE_PASSED && battle.nextBattleId == null) {
            initNextStage(battle)
            return true
        }
        if (battle.status == BattleStatus.LOST || battle.status == BattleStatus.WON) {
            var tmpBattle = battle
            while (tmpBattle.previousBattleId != null) {
                tmpBattle = battleRepository.getOne(tmpBattle.previousBattleId!!)
                tmpBattle.status = battle.status
            }
            return true
        }
        return false
    }

}
