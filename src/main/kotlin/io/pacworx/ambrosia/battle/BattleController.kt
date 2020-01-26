package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.enums.SkillTarget
import io.pacworx.ambrosia.fights.FightRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.hero.HeroSkill
import io.pacworx.ambrosia.maps.MapService
import io.pacworx.ambrosia.maps.SimplePlayerMapTileRepository
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle")
class BattleController(private val battleService: BattleService,
                       private val battleRepository: BattleRepository,
                       private val simplePlayerMapTileRepository: SimplePlayerMapTileRepository,
                       private val mapService: MapService,
                       private val fightRepository: FightRepository,
                       private val resourcesService: ResourcesService,
                       private val heroService: HeroService) {

    @GetMapping("{battleId}")
    @Transactional
    fun getBattle(@ModelAttribute("player") player: Player, @PathVariable battleId: Long): PlayerActionResponse {
        val battle = battleRepository.getOne(battleId)
        return afterBattleAction(player,
            if (battle.status == BattleStatus.INIT) {
                battleService.startBattle(battle)
            } else {
                battle
            }
        )
    }

    @PostMapping
    @Transactional
    fun startPvpBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartDuellRequest): PlayerActionResponse {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        return afterBattleAction(player, battleService.initDuell(player, request))
    }

    @PostMapping("campaign/{mapId}/{posX}/{posY}")
    @Transactional
    fun startCampaign(@ModelAttribute("player") player: Player,
                     @PathVariable mapId: Long,
                     @PathVariable posX: Int,
                     @PathVariable posY: Int,
                     @RequestBody request: StartBattleRequest): PlayerActionResponse {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        val mapTile = simplePlayerMapTileRepository.findPlayerMapTile(player.id, mapId, posX, posY)
        if (mapTile == null || !mapTile.discovered || mapTile.fightId == null || (mapTile.victoriousFight && !mapTile.fightRepeatable)) {
            throw RuntimeException("You cannot fight on that map tile.")
        }
        val fight = fightRepository.getOne(mapTile.fightId)
        val resources = resourcesService.spendResource(player, fight.resourceType, fight.costs)
        return afterBattleAction(player, battleService.initCampaign(player, mapTile, fight, request), resources)
    }

    @PostMapping("{battleId}/{heroPos}/{skillNumber}/{targetPos}")
    @Transactional
    fun takeTurn(@ModelAttribute("player") player: Player,
                 @PathVariable battleId: Long,
                 @PathVariable heroPos: HeroPosition,
                 @PathVariable skillNumber: Int,
                 @PathVariable targetPos: HeroPosition): PlayerActionResponse {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw RuntimeException("You don't own battle $battleId")
        }
        if (battle.activeHero != heroPos) {
            throw RuntimeException("It is not $heroPos's turn on battle $battleId")
        }
        val activeHero = battle.allHeroesAlive().find { it.position == battle.activeHero }
        val skill = activeHero?.heroBase?.skills?.find { it.number == skillNumber }
        if (activeHero == null || activeHero.getCooldown(skillNumber) > 0 || skill == null || skill.passive) {
            throw RuntimeException("Hero $heroPos cannot use skill $skillNumber on battle $battleId")
        }
        val target = battle.allHeroes().find { it.position == targetPos }
                ?: throw RuntimeException("Target $targetPos is not valid on battle $battleId")
        if (!isTargetEligible(battle, activeHero, skill, target)) {
            throw RuntimeException("Target ${target.position} is not valid target for skill ${skill.number} of hero ${activeHero.position} in battle ${battle.id}")
        }
        return afterBattleAction(player, battleService.takeTurn(battle, activeHero, skill, target))
    }

    @PostMapping("{battleId}/{heroPos}/auto")
    @Transactional
    fun takeAutoTurn(@ModelAttribute("player") player: Player,
                     @PathVariable battleId: Long,
                     @PathVariable heroPos: HeroPosition): PlayerActionResponse {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw RuntimeException("You don't own battle $battleId")
        }
        if (battle.activeHero != heroPos) {
            throw RuntimeException("It is not $heroPos's turn on battle $battleId")
        }
        val activeHero = battle.allHeroesAlive().find { it.position == battle.activeHero }
                ?: throw RuntimeException("It is not Hero $heroPos 's turn on battle $battleId")
        return afterBattleAction(player, battleService.takeAutoTurn(battle, activeHero))
    }

    @PostMapping("{battleId}/surrender")
    @Transactional
    fun surrender(@ModelAttribute("player") player: Player,
                  @PathVariable battleId: Long): PlayerActionResponse {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw RuntimeException("You don't own battle $battleId")
        }
        battle.status = BattleStatus.LOST
        return afterBattleAction(player, battle)
    }

    private fun isTargetEligible(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero): Boolean {
        val isPlayer = battle.heroBelongsToPlayer(hero)
        return when (skill.target) {
            SkillTarget.OPPONENT -> isPlayer == battle.heroBelongsToOpponent(target) && (target.isTaunting() || battle.allAlliedHeroesAlive(target).none { it.isTaunting() })
            SkillTarget.SELF -> hero.position == target.position
            SkillTarget.ALL_OWN -> isPlayer == battle.heroBelongsToPlayer(target)
            SkillTarget.OPP_IGNORE_TAUNT -> isPlayer == battle.heroBelongsToOpponent(target)
            SkillTarget.DEAD -> isPlayer == battle.heroBelongsToPlayer(target) && target.status == HeroStatus.DEAD
        }
    }

    private fun afterBattleAction(player: Player, battle: Battle, resources: Resources? = null): PlayerActionResponse {
        return if (battle.status == BattleStatus.WON) {
            val map = battle.mapId?.let {
                mapService.victoriousFight(player, it, battle.mapPosX!!, battle.mapPosY!!)
            }
            val heroes = battle.fight?.let { fight ->
                heroService.wonFight(player, battle.allPlayerHeroes().map { it.heroId }, fight)
            }
            PlayerActionResponse(player = player, resources = resources, currentMap = map, heroes = heroes, ongoingBattle = battle)
        } else {
            PlayerActionResponse(resources = resources, ongoingBattle = battle)
        }
    }
}

data class StartDuellRequest(
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

data class StartBattleRequest(
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?
)
