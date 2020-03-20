package io.pacworx.ambrosia.battle.offline

import io.pacworx.ambrosia.battle.BattleRepository
import io.pacworx.ambrosia.battle.BattleStatus
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.enums.TeamType
import io.pacworx.ambrosia.fights.FightRepository
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.maps.SimplePlayerMapTileRepository
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.data.repository.findByIdOrNull
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
@RequestMapping("battle/mission")
class MissionController(private val simplePlayerMapTileRepository: SimplePlayerMapTileRepository,
                        private val missionService: MissionService,
                        private val battleRepository: BattleRepository,
                        private val fightRepository: FightRepository,
                        private val resourcesService: ResourcesService,
                        private val progressRepository: ProgressRepository,
                        private val vehicleRepository: VehicleRepository,
                        private val heroRepository: HeroRepository,
                        private val missionRepository: MissionRepository,
                        private val lootService: LootService,
                        private val heroService: HeroService
) {

    @PostMapping
    @Transactional
    fun startMission(@ModelAttribute("player") player: Player,
                     @RequestBody request: StartMissionRequest
    ): PlayerActionResponse {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        val mapTile = simplePlayerMapTileRepository.findPlayerMapTile(player.id, request.mapId, request.posX, request.posY)
        if (mapTile == null || !mapTile.discovered || mapTile.fightId == null || !mapTile.victoriousFight || !mapTile.fightRepeatable) {
            throw RuntimeException("You cannot start a mission on that map tile.")
        }
        val progress = progressRepository.getOne(player.id)
        if (request.battleTimes <= 0 || request.battleTimes > progress.maxOfflineBattlesPerMission) {
            throw RuntimeException("Invalid number of battles ${request.battleTimes}/${progress.maxOfflineBattlesPerMission}")
        }
        val fight = fightRepository.getOne(mapTile.fightId)
        val resources = resourcesService.spendResource(player, fight.resourceType, request.battleTimes * fight.costs)

        val vehicle = vehicleRepository.getOne(request.vehicleId)
        if (vehicle.slot == null || vehicle.missionId != null) {
            throw RuntimeException("Vehicle is not ready to start a mission")
        }
        val heroIds = listOfNotNull(request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id)
        if (heroIds.isEmpty()) {
            throw RuntimeException("No heroes selected to start mission")
        }
        heroIds.map { heroRepository.getOne(it) }.forEach { hero ->
            if (hero.playerId != player.id) {
                throw RuntimeException("You have to own the heroes sending to a mission")
            }
            if (hero.missionId != null) {
                throw RuntimeException("You can only send heroes to a mission that are not already on a mission")
            }
        }

        val mission = missionService.executeMission(player, progress, mapTile, fight, vehicle, request)
        return PlayerActionResponse(resources = resources, missions = listOf(mission))
    }


    @PostMapping("{missionId}")
    @Transactional
    fun checkMission(@PathVariable missionId: Long): PlayerActionResponse {
        val mission = missionRepository.findByIdOrNull(missionId)
            ?: return PlayerActionResponse(missionIdFinished = missionId)
        missionService.check(mission)
        return PlayerActionResponse(missions = listOf(mission))
    }

    @PostMapping("{missionId}/collect")
    @Transactional
    fun collectLoot(@ModelAttribute("player") player: Player, @PathVariable missionId: Long): PlayerActionResponse {
        val mission = missionRepository.findByIdOrNull(missionId)
            ?: return PlayerActionResponse(missionIdFinished = missionId)
        if (mission.playerId != player.id) {
            throw RuntimeException("Cannot collect a mission you are not owning")
        }
        if (!mission.isMissionFinished()) {
            throw RuntimeException("Cannot collect a mission that is still ongoing")
        }

        val vehicle = vehicleRepository.getOne(mission.vehicleId)
        val heroes = heroService.wonFight(
            player,
            listOfNotNull(mission.hero1Id, mission.hero2Id, mission.hero3Id, mission.hero4Id),
            mission.fight,
            vehicle
        ) ?: listOf()

        val lootItems = mission.battles.flatMap { battle ->
            if (battle.battleWon) {
                val lootBoxResult = lootService.openLootBox(player, mission.fight.lootBox, vehicle)
                battle.looted = lootBoxResult.items.map { lootService.asLooted(it) }
                lootBoxResult.items
            } else {
                listOf()
            }
        }

        missionRepository.delete(mission)
        return PlayerActionResponse(
            player = player,
            resources = resourcesService.getResources(player),
            heroes = heroes + lootItems.filter { it.hero != null }.map { it.hero!! },
            gears = lootItems.filter { it.gear != null }.map { it.gear!! }.takeIf { it.isNotEmpty() },
            jewelries = lootItems.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicles = lootItems.filter { it.vehicle != null }.map { it.vehicle!! }.takeIf { it.isNotEmpty() },
            vehicleParts = lootItems.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            missions = listOf(mission),
            missionIdFinished = missionId
        )
    }

}

data class StartMissionRequest(
    val type: TeamType,
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
