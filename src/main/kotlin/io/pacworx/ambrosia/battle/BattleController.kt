package io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.achievements.Achievements
import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.exceptions.MapTileActionException
import io.pacworx.ambrosia.exceptions.OngoingBattleException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.fights.FightRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.hero.skills.HeroSkill
import io.pacworx.ambrosia.hero.skills.SkillTarget
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.loot.LootedType
import io.pacworx.ambrosia.maps.MapService
import io.pacworx.ambrosia.maps.SimplePlayerMapTileRepository
import io.pacworx.ambrosia.oddjobs.OddJob
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.Resources
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.team.TeamType
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
class BattleController(
    private val battleService: BattleService,
    private val battleRepository: BattleRepository,
    private val simplePlayerMapTileRepository: SimplePlayerMapTileRepository,
    private val mapService: MapService,
    private val fightRepository: FightRepository,
    private val resourcesService: ResourcesService,
    private val progressRepository: ProgressRepository,
    private val heroService: HeroService,
    private val lootService: LootService,
    private val auditLogService: AuditLogService,
    private val battleStepRepository: BattleStepRepository,
    private val oddJobService: OddJobService,
    private val achievementsRepository: AchievementsRepository
) {

    @GetMapping("{battleId}")
    @Transactional
    fun getBattle(@ModelAttribute("player") player: Player, @PathVariable battleId: Long): PlayerActionResponse {
        val battle = battleRepository.findByIdOrNull(battleId) ?: throw EntityNotFoundException(player, "battle", battleId)
        battle.steps = battleStepRepository.findAllByBattleIdOrderByTurnAscPhaseAscIdAsc(battle.id).toMutableList()
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
        if (battleService.getOngoingBattle(player) != null) {
            throw OngoingBattleException(player)
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
        if (battleService.getOngoingBattle(player) != null) {
            throw OngoingBattleException(player)
        }
        val mapTile = simplePlayerMapTileRepository.findPlayerMapTile(player.id, mapId, posX, posY)
        if (mapTile == null || !mapTile.discovered || mapTile.fightId == null || (mapTile.victoriousFight && !mapTile.fightRepeatable)) {
            throw MapTileActionException(player, "battle on", mapId, posX, posY)
        }
        val fight = fightRepository.getOne(mapTile.fightId)
        val resources = resourcesService.spendResource(player, fight.resourceType, fight.costs)
        val achievements = achievementsRepository.getOne(player.id)
        achievements.resourceSpend(fight.resourceType, fight.costs)
        val oddJobsEffected = oddJobService.resourcesSpend(player, fight.resourceType, fight.costs)
        val battle = battleService.initCampaign(player, mapTile, fight, request)
        auditLogService.log(player, "Started a campaign battle #${battle.id} (status: ${battle.status.name}): fight #${mapTile.fightId} on map #${mapTile.mapId} ${mapTile.posX}x${mapTile.posY} " +
                "using ${battle.vehicle?.let { "${it.baseVehicle.name} #${it.id} in slot ${it.slot}" } ?: "no vehicle"} and heroes ${battle.allPlayerHeroes().joinToString { "${it.heroBase.name} level ${it.level}" }} " +
                "paying ${fight.costs} ${fight.resourceType.name}"
        )
        return afterBattleAction(player, battle, resources, oddJobsEffected.toMutableList(), achievements)
    }

    @PostMapping("campaign/test/{fightId}")
    @Transactional
    fun startCampaignTest(@ModelAttribute("player") player: Player,
                          @PathVariable fightId: Long,
                          @RequestBody request: StartBattleRequest): PlayerActionResponse {
        if (battleService.getOngoingBattle(player) != null) {
            throw OngoingBattleException(player)
        }
        val fight = fightRepository.getOne(fightId)
        return afterBattleAction(player, battleService.initCampaign(player, null, fight, request))
    }

    @PostMapping("repeat/test/{battleId}")
    @Transactional
    fun repeatTestBattle(@ModelAttribute("player") player: Player,
                         @PathVariable battleId: Long): PlayerActionResponse {
        if (battleService.getOngoingBattle(player) != null) {
            throw OngoingBattleException(player)
        }
        val prevBattle = battleRepository.findByIdOrNull(battleId) ?: throw EntityNotFoundException(player, "battle", battleId)
        if (prevBattle.type != BattleType.TEST) {
            throw GeneralException(player, "Invalid action", "Cannot repeat a battle other than test battles")
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
        val battle = battleRepository.findByIdOrNull(battleId) ?: throw EntityNotFoundException(player, "battle", battleId)
        if (battle.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own battle #$battleId")
        }
        if (battle.activeHero != heroPos) {
            throw GeneralException(player, "Not hero's turn", "It is not $heroPos's turn on battle $battleId")
        }
        val activeHero = battle.allHeroesAlive().find { it.position == battle.activeHero }
        val skill = activeHero?.heroBase?.skills?.find { it.number == skillNumber }
        if (activeHero == null || activeHero.getCooldown(skillNumber) > 0 || skill == null || skill.passive || activeHero.getSkillLevel(skill.number) <= 0) {
            throw GeneralException(player, "Invalid skill", "Hero $heroPos cannot use skill $skillNumber on battle $battleId")
        }
        val target = battle.allHeroes().find { it.position == targetPos }
                ?: throw GeneralException(player, "Invalid target", "Target $targetPos is not valid on battle $battleId")
        if (!isTargetEligible(battle, activeHero, skill, target)) {
            throw GeneralException(player, "Invalid target", "Target ${target.position} is not valid target for skill ${skill.number} of hero ${activeHero.position} in battle ${battle.id}")
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
            throw UnauthorizedException(player, "You don't own battle #$battleId")
        }
        if (battle.activeHero != heroPos) {
            throw GeneralException(player, "Not hero's turn", "It is not $heroPos's turn on battle $battleId")
        }
        val activeHero = battle.allHeroesAlive().find { it.position == battle.activeHero }
                ?: throw GeneralException(player, "Not hero's turn", "It is not $heroPos's turn on battle $battleId")
        return afterBattleAction(player, battleService.takeAutoTurn(battle, activeHero))
    }

    @PostMapping("{battleId}/surrender")
    @Transactional
    fun surrender(@ModelAttribute("player") player: Player,
                  @PathVariable battleId: Long): PlayerActionResponse {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own battle #$battleId")
        }
        battle.status = BattleStatus.LOST
        battleService.battleEnded(battle)
        if (battle.type == BattleType.CAMPAIGN) {
            auditLogService.log(player, "Surrendered battle #${battle.id}")
        }
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

    private fun afterBattleAction(player: Player, battle: Battle, resources: Resources? = null, oddJobsEffected: MutableList<OddJob> = mutableListOf(), achievements: Achievements? = null): PlayerActionResponse {
        return if (battle.status == BattleStatus.WON && battle.type != BattleType.TEST) {
            val battleAchievements = achievements ?: achievementsRepository.getOne(player.id)
            val map = battle.mapId?.let {
                mapService.victoriousFight(player, it, battle.mapPosX!!, battle.mapPosY!!)
            }
            val progress = progressRepository.getOne(player.id)
            val heroes = heroService.wonFight(player, progress, battle.allPlayerHeroes().map { it.heroId }, battle.fight, battle.vehicle)
            val loot = battle.fight?.lootBox?.let { lootService.openLootBox(player, it, battleAchievements, battle.vehicle) }
            if (battle.type == BattleType.CAMPAIGN) {
                auditLogService.log(player, "Won battle #${battle.id} releasing ${battle.vehicle?.let { "vehicle ${it.baseVehicle.name} #${it.id} in slot ${it.slot}" } ?: "no vehicle"} " +
                        "and heroes ${battle.allPlayerHeroes().joinToString { "${it.heroBase.name} level ${it.level}" }}. " +
                        "Looting ${loot?.items?.joinToString { it.auditLog() } ?: "nothing"}"
                )
            }
            oddJobsEffected.addAll((loot?.let { oddJobService.looted(player, it.items) } ?: emptyList()))
            PlayerActionResponse(
                player = player,
                progress = progress,
                achievements = battleAchievements,
                resources = loot?.let { resourcesService.getResources(player) } ?: resources,
                currentMap = map,
                heroes = (heroes ?: listOf()) + (loot?.items?.filter { it.hero != null }?.map { it.hero!! } ?: listOf()),
                gears = loot?.items?.filter { it.gear != null }?.map { it.gear!! }?.takeIf { it.isNotEmpty() },
                jewelries = loot?.items?.filter { it.jewelry != null }?.map { it.jewelry!! }?.takeIf { it.isNotEmpty() },
                vehicles = loot?.items?.filter { it.vehicle != null }?.map { it.vehicle!! }?.takeIf { it.isNotEmpty() },
                vehicleParts = loot?.items?.filter { it.vehiclePart != null }?.map { it.vehiclePart!! }?.takeIf { it.isNotEmpty() },
                ongoingBattle = battle,
                looted = loot?.items?.let { items -> Looted(LootedType.BATTLE, items.map { lootService.asLootedItem(it) })  },
                oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() }
            )
        } else {
            if (battle.type == BattleType.CAMPAIGN && battle.status == BattleStatus.LOST) {
                auditLogService.log(player, "Lost battle #${battle.id} releasing ${battle.vehicle?.let { "vehicle ${it.baseVehicle.name} #${it.id} in slot ${it.slot}" } ?: "no vehicle"} " +
                        "and heroes ${battle.allPlayerHeroes().joinToString { "${it.heroBase.name} level ${it.level}" }}"
                )
            }
            PlayerActionResponse(
                resources = resources,
                achievements = achievements,
                ongoingBattle = battle,
                oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() }
            )
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
