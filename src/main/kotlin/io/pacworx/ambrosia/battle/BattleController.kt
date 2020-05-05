package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.hero.skills.SkillTarget
import io.pacworx.ambrosia.team.TeamType
import io.pacworx.ambrosia.fights.FightRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.hero.skills.HeroSkill
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.maps.MapService
import io.pacworx.ambrosia.maps.SimplePlayerMapTileRepository
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
                       private val heroService: HeroService,
                       private val lootService: LootService) {

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
    fun startTestDuellBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartDuellRequest): PlayerActionResponse {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        return afterBattleAction(player, battleService.initTestDuell(player, request))
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

    @PostMapping("campaign/test/{fightId}")
    @Transactional
    fun startCampaignTest(@ModelAttribute("player") player: Player,
                          @PathVariable fightId: Long,
                          @RequestBody request: StartBattleRequest): PlayerActionResponse {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        val fight = fightRepository.getOne(fightId)
        return afterBattleAction(player, battleService.initCampaign(player, null, fight, request))
    }

    @PostMapping("repeat/test/{battleId}")
    @Transactional
    fun repeatTestBattle(@ModelAttribute("player") player: Player,
                         @PathVariable battleId: Long): PlayerActionResponse {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        val prevBattle = battleRepository.findByIdOrNull(battleId) ?: throw RuntimeException("Cannot repeat battle #$battleId bc it doesn't exist")
        if (prevBattle.type != BattleType.TEST) {
            throw RuntimeException("Cannot repeat a battle other than test battles")
        }
        return afterBattleAction(player, battleService.repeatTestBattle(prevBattle))
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
        return if (battle.status == BattleStatus.WON && battle.type != BattleType.TEST) {
            val map = battle.mapId?.let {
                mapService.victoriousFight(player, it, battle.mapPosX!!, battle.mapPosY!!)
            }
            val heroes = heroService.wonFight(player, battle.allPlayerHeroes().map { it.heroId }, battle.fight, battle.vehicle)
            val loot = battle.fight?.lootBox?.let { lootService.openLootBox(player, it, battle.vehicle) }
            PlayerActionResponse(
                player = player,
                resources = loot?.let { resourcesService.getResources(player) } ?: resources,
                currentMap = map,
                heroes = (heroes ?: listOf()) + (loot?.items?.filter { it.hero != null }?.map { it.hero!! } ?: listOf()),
                gears = loot?.items?.filter { it.gear != null }?.map { it.gear!! }?.takeIf { it.isNotEmpty() },
                jewelries = loot?.items?.filter { it.jewelry != null }?.map { it.jewelry!! }?.takeIf { it.isNotEmpty() },
                vehicles = loot?.items?.filter { it.vehicle != null }?.map { it.vehicle!! }?.takeIf { it.isNotEmpty() },
                vehicleParts = loot?.items?.filter { it.vehiclePart != null }?.map { it.vehiclePart!! }?.takeIf { it.isNotEmpty() },
                ongoingBattle = battle,
                looted = loot?.items?.map { lootService.asLooted(it) }
            )
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
    val type: TeamType,
    val vehicleId: Long?,
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?
)
