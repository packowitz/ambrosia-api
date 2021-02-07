package io.pacworx.ambrosia.battle.offline

import io.pacworx.ambrosia.battle.*
import io.pacworx.ambrosia.fights.Fight
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.maps.SimplePlayerMapTile
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.speedup.SpeedupService
import io.pacworx.ambrosia.speedup.SpeedupType
import io.pacworx.ambrosia.vehicle.Vehicle
import io.pacworx.ambrosia.vehicle.VehicleService
import io.pacworx.ambrosia.vehicle.VehicleStat
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MissionService(
    private val battleService: BattleService,
    private val aiService: AiService,
    private val battleRepository: BattleRepository,
    private val offlineBattleRepository: OfflineBattleRepository,
    private val missionRepository: MissionRepository,
    private val heroRepository: HeroRepository,
    private val vehicleService: VehicleService,
    private val speedupService: SpeedupService) {

    private val log = KotlinLogging.logger {}

    fun getAllMissions(player: Player): List<Mission> {
        return missionRepository.findAllByPlayerIdOrderBySlotNumber(player.id).onEach { check(it) }
    }

    fun check(mission: Mission) {
        val now = Instant.now()
        mission.wonCount = 0
        mission.lostCount = 0
        mission.battles.forEach { battle ->
            battle.battleStarted = battle.battleStarted || battle.startTimestamp.isBefore(now)
            if (battle.battleStarted && !battle.battleFinished) {
                if (battle.finishTimestamp.isBefore(now)) {
                    battle.battleFinished = true
                    if (battle.battleWon) {
                        mission.wonCount ++
                    } else {
                        mission.lostCount ++
                    }
                }
            }
        }
        enrichSpeedup(mission)
    }

    fun enrichSpeedup(mission: Mission) {
        if (mission.getSecondsUntilDone() > 0) {
            mission.speedup = speedupService.speedup(SpeedupType.MISSION, mission.getDuration(), mission.getSecondsUntilDone())
        } else {
            mission.speedup = null
        }
    }

    fun executeMission(player: Player, progress: Progress, mapTile: SimplePlayerMapTile, fight: Fight, vehicle: Vehicle, request: StartMissionRequest): Mission {
        val startBattleRequest = StartBattleRequest(
            type = request.type,
            vehicleId = request.vehicleId,
            hero1Id = request.hero1Id,
            hero2Id = request.hero2Id,
            hero3Id = request.hero3Id,
            hero4Id = request.hero4Id
        )
        val battleSpeed = progress.offlineBattleSpeed + vehicleService.getStat(vehicle, VehicleStat.OFFLINE_BATTLE_SPEED)
        val missionStartTimestamp = Instant.now()
        var startTimestamp: Instant?
        var finishTimestamp: Instant? = null
        val battles = (1..request.battleTimes).map {
            log.info("Starting Battle $it of ${request.battleTimes} for player ${player.id}")
            val battle = battleService.initCampaign(player, mapTile, fight, startBattleRequest)
            val turns = executeBattle(battle)
            val durationInMs = (1000 * fight.travelDuration) + ((turns * fight.timePerTurn * 100) / battleSpeed)
            startTimestamp = finishTimestamp ?: missionStartTimestamp
            finishTimestamp = startTimestamp!!.plusMillis(durationInMs.toLong())
            offlineBattleRepository.save(OfflineBattle(
                battleId = battle.id,
                startTimestamp = startTimestamp!!,
                finishTimestamp = finishTimestamp!!,
                battleWon = battle.status == BattleStatus.WON
            ))
        }

        val mission = missionRepository.save(Mission(
            playerId = player.id,
            fight = fight,
            mapId = mapTile.mapId,
            posX = mapTile.posX,
            posY = mapTile.posY,
            vehicleId = vehicle.id,
            slotNumber = vehicle.slot!!,
            hero1Id = request.hero1Id,
            hero2Id = request.hero2Id,
            hero3Id = request.hero3Id,
            hero4Id = request.hero4Id,
            totalCount = request.battleTimes,
            startTimestamp = missionStartTimestamp,
            finishTimestamp = finishTimestamp!!
        ))
        mission.battles = battles

        vehicle.missionId = mission.id
        listOfNotNull(request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id)
            .map { heroRepository.getOne(it) }
            .forEach { hero -> hero.missionId = mission.id }
        check(mission)
        return mission
    }

    private fun executeBattle(battle: Battle): Int {
        while (!battleService.battleEnded(battle)) {
            act(battle)
            if (battle.turnsDone > battle.fight!!.maxTurnsPerStage) {
                battle.status = BattleStatus.LOST
            }
        }
        return battle.nextBattleId?.let { nextBattleid ->
            log.info("Starting next stage for battle ${battle.id} for player ${battle.playerId} with battleId $nextBattleid")
            val nextStageBattle = battleRepository.getOne(nextBattleid)
            battle.turnsDone + executeBattle(nextStageBattle)
        } ?: battle.turnsDone
    }

    private fun act(battle: Battle) {
        when(battle.status) {
            BattleStatus.INIT -> battleService.startBattle(battle)
            BattleStatus.PLAYER_TURN -> {
                aiService.doAction(battle, battle.allHeroesAlive().find { it.position == battle.activeHero }!!)
                if (!battleService.battleEnded(battle)) {
                    battleService.nextTurn(battle)
                }
            }
            BattleStatus.OPP_TURN -> battleService.nextTurn(battle)
            else -> {}
        }
    }
}
