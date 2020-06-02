package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.exceptions.ConfigurationException
import io.pacworx.ambrosia.exceptions.HeroBusyException
import io.pacworx.ambrosia.exceptions.VehicleBusyException
import io.pacworx.ambrosia.fights.Fight
import io.pacworx.ambrosia.fights.FightRepository
import io.pacworx.ambrosia.fights.stageconfig.SpeedBarChange
import io.pacworx.ambrosia.hero.Hero
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.hero.skills.HeroSkill
import io.pacworx.ambrosia.maps.SimplePlayerMapTile
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.team.Team
import io.pacworx.ambrosia.team.TeamRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import io.pacworx.ambrosia.vehicle.VehicleService
import io.pacworx.ambrosia.vehicle.VehicleStat
import mu.KotlinLogging
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import kotlin.math.min

@Service
class BattleService(private val playerRepository: PlayerRepository,
                    private val heroService: HeroService,
                    private val battleRepository: BattleRepository,
                    private val skillService: SkillService,
                    private val aiService: AiService,
                    private val propertyService: PropertyService,
                    private val fightRepository: FightRepository,
                    private val teamRepository: TeamRepository,
                    private val vehicleRepository: VehicleRepository,
                    private val vehicleService: VehicleService,
                    private val battleHeroRepository: BattleHeroRepository) {
    private val log = KotlinLogging.logger {}

    companion object {
        const val SPEEDBAR_MAX: Int = 10000
    }

    fun deleteBattle(battle: Battle) {
        var stage = battle
        while (stage.nextBattleId != null) {
            stage = battleRepository.getOne(stage.nextBattleId!!)
            battleRepository.delete(stage)
        }
        battleRepository.delete(battle)
        battle.hero1?.let { battleHeroRepository.delete(it) }
        battle.hero2?.let { battleHeroRepository.delete(it) }
        battle.hero3?.let { battleHeroRepository.delete(it) }
        battle.hero4?.let { battleHeroRepository.delete(it) }
    }

    @Transactional
    fun initTestDuell(player: Player, request: StartDuellRequest): Battle {
        val opponent = playerRepository.getOne(request.oppPlayerId)
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            request.oppHero1Id, request.oppHero2Id, request.oppHero3Id, request.oppHero4Id))
        val battle = battleRepository.save(Battle(
            type = BattleType.TEST,
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
        return initBattle(battle)
    }

    @Transactional
    fun initCampaign(player: Player, mapTile: SimplePlayerMapTile?, fight: Fight, request: StartBattleRequest): Battle {
        val team = teamRepository.findByPlayerIdAndType(player.id, request.type) ?: teamRepository.save(Team ( playerId = player.id, type = request.type))
        team.apply {
            hero1Id = request.hero1Id
            hero2Id = request.hero2Id
            hero3Id = request.hero3Id
            hero4Id = request.hero4Id
        }
        val vehicle = request.vehicleId?.let { vehicleRepository.getOne(it) }?.takeIf { it.playerId == player.id }
        val fightStage = fight.stages.find { it.stage == 1 } ?: throw ConfigurationException("Fight ${fight.name} #${fight.id} has no stages defined.")
        val heroes = heroService.loadHeroes(listOfNotNull(
            request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
            fightStage.hero1Id, fightStage.hero2Id, fightStage.hero3Id, fightStage.hero4Id))
        if (vehicle != null) {
            if (vehicle.slot == null || vehicle.missionId != null || vehicle.upgradeTriggered) {
                throw VehicleBusyException(player, vehicle)
            }
        }
        heroes.find { it.playerId == player.id && it.missionId != null }?.let { throw HeroBusyException(player, it) }

        val battle = battleRepository.save(Battle(
            type = mapTile?.let { BattleType.CAMPAIGN } ?: BattleType.TEST,
            fight = fight,
            fightStage = fightStage.stage,
            mapId = mapTile?.mapId,
            mapPosX = mapTile?.posX,
            mapPosY = mapTile?.posY,
            vehicle = vehicle,
            playerId = player.id,
            playerName = player.name,
            opponentName = fight.name + " Stage-" + fightStage.stage,
            hero1 = asBattleHero(player.id, request.hero1Id, HeroPosition.HERO1, heroes),
            hero2 = asBattleHero(player.id, request.hero2Id, HeroPosition.HERO2, heroes),
            hero3 = asBattleHero(player.id, request.hero3Id, HeroPosition.HERO3, heroes),
            hero4 = asBattleHero(player.id, request.hero4Id, HeroPosition.HERO4, heroes),
            oppHero1 = asBattleHero(heroId = fightStage.hero1Id, position = HeroPosition.OPP1, heroes = heroes),
            oppHero2 = asBattleHero(heroId = fightStage.hero2Id, position = HeroPosition.OPP2, heroes = heroes),
            oppHero3 = asBattleHero(heroId = fightStage.hero3Id, position = HeroPosition.OPP3, heroes = heroes),
            oppHero4 = asBattleHero(heroId = fightStage.hero4Id, position = HeroPosition.OPP4, heroes = heroes)
        ))
        return initBattle(battle)
    }

    fun repeatTestBattle(prevBattle: Battle): Battle {
        val fightStage = prevBattle.fight?.stages?.find { it.stage == 1 }
        val heroIds = mutableListOf(prevBattle.hero1?.heroId, prevBattle.hero2?.heroId, prevBattle.hero3?.heroId, prevBattle.hero4?.heroId)
        if (fightStage != null) {
            heroIds.add(fightStage.hero1Id)
            heroIds.add(fightStage.hero2Id)
            heroIds.add(fightStage.hero3Id)
            heroIds.add(fightStage.hero4Id)
        } else {
            heroIds.add(prevBattle.oppHero1?.heroId)
            heroIds.add(prevBattle.oppHero2?.heroId)
            heroIds.add(prevBattle.oppHero3?.heroId)
            heroIds.add(prevBattle.oppHero4?.heroId)
        }
        val heroes = heroService.loadHeroes(heroIds.filterNotNull())
        val battle = battleRepository.save(Battle(
            type = prevBattle.type,
            fight = prevBattle.fight,
            fightStage = fightStage?.stage,
            mapId = prevBattle.mapId,
            mapPosX = prevBattle.mapPosX,
            mapPosY = prevBattle.mapPosY,
            playerId = prevBattle.playerId,
            playerName = prevBattle.playerName,
            opponentId = prevBattle.opponentId,
            opponentName = prevBattle.opponentName,
            hero1 = asBattleHero(prevBattle.playerId, prevBattle.hero1?.heroId, HeroPosition.HERO1, heroes),
            hero2 = asBattleHero(prevBattle.playerId, prevBattle.hero2?.heroId, HeroPosition.HERO2, heroes),
            hero3 = asBattleHero(prevBattle.playerId, prevBattle.hero3?.heroId, HeroPosition.HERO3, heroes),
            hero4 = asBattleHero(prevBattle.playerId, prevBattle.hero4?.heroId, HeroPosition.HERO4, heroes),
            oppHero1 = asBattleHero(heroId = if (fightStage != null) fightStage.hero1Id else prevBattle.oppHero1?.heroId, position = HeroPosition.OPP1, heroes = heroes),
            oppHero2 = asBattleHero(heroId = if (fightStage != null) fightStage.hero2Id else prevBattle.oppHero2?.heroId, position = HeroPosition.OPP2, heroes = heroes),
            oppHero3 = asBattleHero(heroId = if (fightStage != null) fightStage.hero3Id else prevBattle.oppHero3?.heroId, position = HeroPosition.OPP3, heroes = heroes),
            oppHero4 = asBattleHero(heroId = if (fightStage != null) fightStage.hero4Id else prevBattle.oppHero4?.heroId, position = HeroPosition.OPP4, heroes = heroes)
        ))
        return initBattle(battle)
    }

    private fun initBattle(battle: Battle): Battle {
        battle.allHeroes().shuffled().forEachIndexed { idx, hero ->
            hero.priority = idx
        }
        battle.vehicle?.let { vehicleService.applyPartsToBattle(it, battle) }
        return startBattle(battle)
    }

    @Transactional
    fun startBattle(battle: Battle): Battle {
        battle.applyBonuses(propertyService)
        nextTurn(battle)
        return battle
    }

    @Transactional
    fun takeTurn(battle: Battle, activeHero: BattleHero, skill: HeroSkill, target: BattleHero): Battle {
        log.info("Battle ${battle.id} turn ${battle.turnsDone} manual: hero ${activeHero.position} uses S${skill.number} on ${target.position}")
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
        heroId?.let { heroes.find { hero -> hero.id == it }?.let {
            BattleHero(playerId, heroService.asHeroDto(it), it.heroBase, position)
        }}

    fun nextTurn(battle: Battle) {
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
                if (activeHero.status == HeroStatus.CONFUSED && battle.allAlliedHeroesAlive(activeHero).none { it.position != activeHero.position }) {
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
            battle.allHeroesAlive().forEach {hero ->
                var speedBarFilling = hero.heroSpeed + hero.speedBonus
                if (battle.heroBelongsToPlayer(hero)) {
                    battle.fight?.environment?.playerSpeedBarSlowed?.takeIf { it > 0 }?.let { decrease ->
                        speedBarFilling -= (speedBarFilling * decrease) / 100
                    }
                } else {
                    battle.fight?.environment?.oppSpeedBarFastened?.takeIf { it > 0 }?.let { increase ->
                        speedBarFilling += (speedBarFilling * increase) / 100
                    }
                }
                hero.currentSpeedBar += speedBarFilling
            }
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

    fun battleEnded(battle: Battle): Boolean {
        if (battle.status == BattleStatus.STAGE_PASSED) {
            if (battle.nextBattleId == null) {
                initNextStage(battle)
            }
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

    private fun initNextStage(battle: Battle) {
        val fight = fightRepository.getOne(battle.fight!!.id)
        applyStageFinishedEffects(battle, fight)
        val nextStage = fight.stages.find { it.stage > battle.fightStage!! } ?: throw ConfigurationException("Fight ${fight.name} has no next stage defined.")
        val heroes = heroService.loadHeroes(listOfNotNull(
            nextStage.hero1Id, nextStage.hero2Id, nextStage.hero3Id, nextStage.hero4Id))
        val nextBattle = battleRepository.save(Battle(
            type = BattleType.CAMPAIGN,
            previousBattleId = battle.id,
            fight = fight,
            fightStage = nextStage.stage,
            mapId = battle.mapId,
            mapPosX = battle.mapPosX,
            mapPosY = battle.mapPosY,
            playerId = battle.playerId,
            playerName = battle.playerName,
            opponentName = fight.name + " Stage-" + nextStage.stage,
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

    private fun applyStageFinishedEffects(battle: Battle, fight: Fight) {
        val config = fight.stageConfig
        if (config.debuffsRemoved) {
            battle.allPlayerHeroesAlive().forEach { hero -> hero.buffs.removeIf { it.buff.type == BuffType.DEBUFF } }
        } else if (config.debuffDurationChange != 0) {
            battle.allPlayerHeroesAlive().forEach { hero ->
                hero.buffs.filter { it.buff.type == BuffType.DEBUFF }.forEach { it.duration += config.debuffDurationChange }
            }
            battle.allPlayerHeroesAlive().forEach { hero -> hero.buffs.removeIf { it.duration <= 0 } }
        }
        if (config.buffsRemoved) {
            battle.allPlayerHeroesAlive().forEach { hero -> hero.buffs.removeIf { it.buff.type == BuffType.BUFF } }
        } else if (config.buffDurationChange != 0) {
            battle.allPlayerHeroesAlive().forEach { hero ->
                hero.buffs.filter { it.buff.type == BuffType.BUFF }.forEach { it.duration += config.buffDurationChange }
            }
            battle.allPlayerHeroesAlive().forEach { hero -> hero.buffs.removeIf { it.duration <= 0 } }
        }
        if (config.hpHealing > 0) {
            val healPerc = config.hpHealing + (config.hpHealing * vehicleService.getStat(battle.vehicle, VehicleStat.STAGE_HEAL) / 100)
            battle.allPlayerHeroesAlive().forEach { hero ->
                hero.currentHp = min(hero.currentHp + (hero.heroHp * healPerc / 100), hero.heroHp)
            }
        }
        if (config.armorRepair > 0) {
            val repair = config.armorRepair + (config.armorRepair * vehicleService.getStat(battle.vehicle, VehicleStat.STAGE_ARMOR) / 100)
            battle.allPlayerHeroesAlive().forEach { hero ->
                hero.currentArmor = min(hero.currentArmor + (hero.heroArmor * repair / 100), hero.heroArmor)
            }
        }
        when (config.speedBarChange) {
            SpeedBarChange.NONE -> {}
            SpeedBarChange.INITIATIVE -> {
                battle.allPlayerHeroesAlive().forEach { hero ->
                    hero.currentSpeedBar = hero.heroInitiative
                }
            }
            SpeedBarChange.REMOVE -> {
                battle.allPlayerHeroesAlive().forEach { hero ->
                    hero.currentSpeedBar = 0
                }
            }
            SpeedBarChange.HALF -> {
                battle.allPlayerHeroesAlive().forEach { hero ->
                    hero.currentSpeedBar = (0.5 * hero.currentSpeedBar).toInt()
                }
            }
        }
    }

}
