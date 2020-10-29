package io.pacworx.ambrosia.battle.offline

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.battle.BattleService
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.*
import io.pacworx.ambrosia.fights.FightRepository
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.maps.SimplePlayerMapTileRepository
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.team.Team
import io.pacworx.ambrosia.team.TeamRepository
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle/mission")
class MissionController(
    private val simplePlayerMapTileRepository: SimplePlayerMapTileRepository,
    private val missionService: MissionService,
    private val battleService: BattleService,
    private val fightRepository: FightRepository,
    private val resourcesService: ResourcesService,
    private val progressRepository: ProgressRepository,
    private val vehicleRepository: VehicleRepository,
    private val heroRepository: HeroRepository,
    private val missionRepository: MissionRepository,
    private val lootService: LootService,
    private val heroService: HeroService,
    private val auditLogService: AuditLogService,
    private val oddJobService: OddJobService,
    private val achievementsRepository: AchievementsRepository,
    private val inboxMessageRepository: InboxMessageRepository,
    private val teamRepository: TeamRepository
) {

    @PostMapping
    @Transactional
    fun startMission(@ModelAttribute("player") player: Player,
                     @RequestBody request: StartMissionRequest
    ): PlayerActionResponse {
        if (battleService.getOngoingBattle(player) != null) {
            throw OngoingBattleException(player)
        }
        val mapTile = simplePlayerMapTileRepository.findPlayerMapTile(player.id, request.mapId, request.posX, request.posY)
        if (mapTile == null || !mapTile.discovered || mapTile.fightId == null || !mapTile.victoriousFight || !mapTile.fightRepeatable) {
            throw MapTileActionException(player, "start a mission on", request.mapId, request.posX, request.posY)
        }
        val progress = progressRepository.getOne(player.id)
        if (request.battleTimes <= 0 || request.battleTimes > progress.maxOfflineBattlesPerMission) {
            throw GeneralException(player, "Cannot start mission", "Invalid number of battles ${request.battleTimes}/${progress.maxOfflineBattlesPerMission}")
        }
        val fight = fightRepository.findByIdOrNull(mapTile.fightId) ?: throw EntityNotFoundException(player, "fight", mapTile.fightId)
        val resources = resourcesService.spendResource(player, fight.resourceType, request.battleTimes * fight.costs)

        val vehicle = vehicleRepository.findByIdOrNull(request.vehicleId) ?: throw EntityNotFoundException(player, "vehicle", request.vehicleId)
        if (!vehicle.isAvailable()) {
            throw VehicleBusyException(player, vehicle)
        }
        val heroes = listOfNotNull(request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id).let { heroRepository.findAllByPlayerIdAndIdIn(player.id, it) }
        if (heroes.isEmpty()) {
            throw GeneralException(player, "Cannot start mission", "No heroes selected to start mission")
        }
        heroes.forEach { hero ->
            if (!hero.isAvailable()) {
                throw HeroBusyException(player, hero)
            }
        }

        val team = teamRepository.findByPlayerIdAndType(player.id, request.type) ?: teamRepository.save(Team ( playerId = player.id, type = request.type))
        team.apply {
            hero1Id = request.hero1Id
            hero2Id = request.hero2Id
            hero3Id = request.hero3Id
            hero4Id = request.hero4Id
            vehicleId = vehicle.id
        }

        val mission = missionService.executeMission(player, progress, mapTile, fight, vehicle, request)

        auditLogService.log(player, "Start mission #${mission.id} " +
                "(${request.battleTimes}x fight #${mapTile.fightId} on map #${mapTile.mapId} ${mapTile.posX}x${mapTile.posY}) " +
                "with vehicle ${vehicle.baseVehicle.name} #${vehicle.id} in slot ${vehicle.slot} " +
                "and heroes ${heroes.joinToString { "${it.heroBase.name} #${it.id} level ${it.level}" }} " +
                "paying ${request.battleTimes * fight.costs} ${fight.resourceType.name}. Mission takes ${mission.getDuration()} seconds"
        )

        return PlayerActionResponse(
            resources = resources,
            missions = listOf(mission),
            vehicles = listOf(vehicle),
            heroes = heroService.loadHeroes(heroes.map { it.id }).map { heroService.asHeroDto(it) }
        )
    }

    @GetMapping("{missionId}")
    @Transactional
    fun checkMission(@ModelAttribute("player") player: Player, @PathVariable missionId: Long): Mission {
        val mission = missionRepository.findByIdOrNull(missionId)
            ?: throw EntityNotFoundException(player, "mission", missionId)
        missionService.check(mission)
        return mission
    }

    @PostMapping("{missionId}/finish")
    @Transactional
    fun finishMission(@ModelAttribute("player") player: Player, @PathVariable missionId: Long): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val mission = missionRepository.findByIdOrNull(missionId)
            ?: return PlayerActionResponse(missionIdFinished = missionId)
        if (mission.playerId != player.id) {
            throw UnauthorizedException(player, "You do not own that mission")
        }

        val resources = resourcesService.getResources(player)
        val achievements = achievementsRepository.getOne(player.id)
        val progress = progressRepository.getOne(player.id)

        val vehicle = vehicleRepository.getOne(mission.vehicleId)
        val heroes = heroService.wonMission(mission, progress, vehicle)

        mission.battles.filter { !it.battleFinished }.forEach {
            it.cancelled = true
            resourcesService.gainResources(resources, mission.fight.resourceType, mission.fight.costs)
        }
        mission.battles.filter { it.battleFinished }.forEach { _ -> achievements.resourceSpend(mission.fight.resourceType, mission.fight.costs) }
        val lootItems = mission.battles.filter { it.isBattleSuccess() == true }.flatMap { battle ->
            val lootBoxResult = lootService.openLootBox(player, mission.fight.lootBox, achievements, vehicle)
            battle.lootedItems = lootBoxResult.items.map { lootService.asLootedItem(it) }
            lootBoxResult.items
        }
        val oddJobsEffected = oddJobService.missionFinished(player, mission) + oddJobService.looted(player, lootItems) +
            oddJobService.resourcesSpend(player, mission.fight.resourceType, mission.battles.count { it.battleFinished } * mission.fight.costs)

        auditLogService.log(player, "Finish mission #${mission.id} ${mission.battles.count { !it.battleFinished }.takeIf { it > 0 }?.let{ "($it cancelled) " } ?: "" }" +
                "${mission.battles.count { it.battleFinished && it.battleWon } } won ${mission.battles.count { it.battleFinished && !it.battleWon } } lost. " +
                "Releasing vehicle ${vehicle.baseVehicle.name} #${vehicle.id} in slot ${vehicle.slot} " +
                "and heroes ${heroes.joinToString { "${it.heroBase.name} #${it.id} level ${it.level}" }}. " +
                "Looting ${lootItems.joinToString { it.auditLog() }}"
        )

        missionRepository.delete(mission)
        mission.lootCollected = true

        return PlayerActionResponse(
            player = player,
            resources = resources,
            achievements = achievements,
            progress = progress,
            heroes = heroes + lootItems.filter { it.hero != null }.map { it.hero!! },
            gears = lootItems.filter { it.gear != null }.map { it.gear!! }.takeIf { it.isNotEmpty() },
            jewelries = lootItems.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicles = listOf(vehicle) + (lootItems.filter { it.vehicle != null }.map { it.vehicle!! }.takeIf { it.isNotEmpty() } ?: listOf()),
            vehicleParts = lootItems.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            missions = listOf(mission),
            missionIdFinished = missionId,
            oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() },
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1))
        )
    }

}

data class StartMissionRequest(
    val type: String,
    val battleTimes: Int,
    val mapId: Long,
    val posX: Int,
    val posY: Int,
    val vehicleId: Long,
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?
)
