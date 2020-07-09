package io.pacworx.ambrosia.expedition

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.*
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.loot.LootBoxResult
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.loot.LootedItem
import io.pacworx.ambrosia.oddjobs.OddJob
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.vehicle.VehicleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("expedition")
class ExpeditionController(
    private val expeditionRepository: ExpeditionRepository,
    private val playerExpeditionRepository: PlayerExpeditionRepository,
    private val progressRepository: ProgressRepository,
    private val vehicleRepository: VehicleRepository,
    private val heroRepository: HeroRepository,
    private val heroService: HeroService,
    private val lootService: LootService,
    private val resourcesService: ResourcesService,
    private val oddJobService: OddJobService
) {

    @GetMapping("active")
    fun getActiveExpeditions(@ModelAttribute("player") player: Player): List<Expedition> {
        val progress = progressRepository.getOne(player.id)
        if (progress.expeditionLevel > 0) {
            val expeditions = expeditionRepository.findAllByExpeditionBase_LevelAndActiveIsTrue(progress.expeditionLevel)
            val inProgress = playerExpeditionRepository.findAllByPlayerIdAndExpeditionIdIn(player.id, expeditions.map { it.id })
            return expeditions.filter { exp -> inProgress.none { exp.id == it.expeditionId } }
        } else {
            return emptyList()
        }
    }

    @GetMapping("in-progress")
    fun getPlayerExpeditions(@ModelAttribute("player") player: Player): List<PlayerExpedition> {
        return playerExpeditionRepository.findAllByPlayerIdOrderByStartTimestamp(player.id)
    }

    @PostMapping("{expeditionId}/start")
    @Transactional
    fun startExpedition(@ModelAttribute("player") player: Player,
                        @PathVariable expeditionId: Long,
                        @RequestBody request: StartExpeditionRequest): PlayerActionResponse {
        val expedition = expeditionRepository.findByIdOrNull(expeditionId)
            ?: throw EntityNotFoundException(player, "expedition", expeditionId)
        val progress = progressRepository.getOne(player.id)
        if (expedition.expeditionBase.level != progress.expeditionLevel) {
            throw GeneralException(player, "Cannot start expedition", "The expedition you want to start is not your level")
        }
        if (playerExpeditionRepository.findAllByPlayerIdAndExpeditionIdIn(player.id, listOf(expeditionId)).isNotEmpty()) {
            throw GeneralException(player, "Cannot start expedition", "You are already on that expedition")
        }
        val vehicle = vehicleRepository.findByIdOrNull(request.vehicleId)?.takeIf { it.playerId == player.id }
            ?: throw EntityNotFoundException(player, "vehicle", request.vehicleId)
        if (!vehicle.isAvailable()) {
            throw VehicleBusyException(player, vehicle)
        }
        val heroes = heroRepository.findAllByPlayerIdAndIdIn(
            player.id,
            listOfNotNull(request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id)
        )
        if (heroes.isEmpty()) {
            throw GeneralException(player, "Cannot start expedition", "No heroes are assigned for this expedition")
        }
        heroes.forEach { hero ->
            if (!hero.isAvailable()) {
                throw HeroBusyException(player, hero)
            }
        }

        val now = Instant.now()
        val until = now.plus(expedition.expeditionBase.durationMinutes.toLong(), ChronoUnit.MINUTES)
        val playerExpedition = playerExpeditionRepository.save(PlayerExpedition(
            playerId = player.id,
            expeditionId = expeditionId,
            vehicleId = vehicle.id,
            hero1Id = request.hero1Id,
            hero2Id = request.hero2Id,
            hero3Id = request.hero3Id,
            hero4Id = request.hero4Id,
            completed = false,
            name = expedition.expeditionBase.name,
            description = expedition.expeditionBase.description,
            level = expedition.expeditionBase.level,
            rarity = expedition.expeditionBase.rarity,
            startTimestamp = now,
            finishTimestamp = until
        ))
        vehicle.playerExpeditionId = playerExpedition.id
        heroes.forEach { it.playerExpeditionId = playerExpedition.id }
        return PlayerActionResponse(
            vehicles = listOf(vehicle),
            heroes = heroes.map { heroService.asHeroDto(it) },
            playerExpeditions = listOf(playerExpedition)
        )
    }

    @PostMapping("{playerExpeditionId}/finish")
    @Transactional
    fun finishExpedition(
        @ModelAttribute("player") player: Player,
        @PathVariable playerExpeditionId: Long
    ): PlayerActionResponse {
        val playerExpedition = playerExpeditionRepository.findByIdOrNull(playerExpeditionId)
            ?: throw EntityNotFoundException(player, "playerExpedition", playerExpeditionId)
        if (playerExpedition.playerId != player.id) {
            throw UnauthorizedException(player, "You don't own that expedition")
        }
        if (playerExpedition.completed) {
            throw GeneralException(player, "Cannot finish expedition", "Expedition is already completed")
        }
        playerExpedition.completed = true
        val vehicle = vehicleRepository.findByIdOrNull(playerExpedition.vehicleId)
            ?: throw EntityNotFoundException(player, "vehicle", playerExpedition.vehicleId)
        var xp = 0
        var oddJobsEffected: List<OddJob>? = null
        var cancelledId: Long? = null
        var lootBoxResult: LootBoxResult? = null
        if (playerExpedition.getSecondsUntilDone() <= 2) {
            val expedition = expeditionRepository.findByIdOrNull(playerExpedition.expeditionId)
                ?: throw EntityNotFoundException(player, "expedition", playerExpedition.expeditionId)
            xp = expedition.expeditionBase.xp
            val oddJob = oddJobService.createOddJob(player, playerExpedition.level)
            lootBoxResult = lootService.openLootBox(player, expedition.expeditionBase.lootBoxId, vehicle)
            playerExpedition.lootedItems = lootBoxResult.items.map { lootService.asLootedItem(it) } +
                listOfNotNull(oddJob?.let { LootedItem(
                    type = LootItemType.RESOURCE,
                    resourceType = ResourceType.ODD_JOB,
                    value = 1
                )})
            oddJobsEffected = listOfNotNull(oddJob) + oddJobService.expeditionFinished(player, expedition)
        } else {
            // expedition got cancelled
            cancelledId = playerExpeditionId
            playerExpeditionRepository.delete(playerExpedition)
        }
        val heroes = heroService.finishedExpedition(playerExpedition, vehicle, xp)

        val lootItems = lootBoxResult?.items ?: emptyList()
        return PlayerActionResponse(
            resources = if (lootItems.any { it.resource != null }) { resourcesService.getResources(player) } else { null },
            progress = if (lootItems.any { it.progress != null }) { progressRepository.getOne(player.id) } else { null },
            vehicles = listOf(vehicle) + lootItems.filter { it.vehicle != null }.map { it.vehicle!! },
            heroes = heroes + lootItems.filter { it.hero != null }.map { it.hero!! },
            gears = lootItems.filter { it.gear != null }.map { it.gear!! }.takeIf { it.isNotEmpty() },
            jewelries = lootItems.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicleParts = lootItems.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            playerExpeditions = playerExpedition.takeIf { cancelledId == null }?.let { listOf(it) },
            playerExpeditionCancelled = cancelledId,
            oddJobs = oddJobsEffected?.takeIf { it.isNotEmpty() }
        )
    }

}

data class StartExpeditionRequest(
    val vehicleId: Long,
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?
)