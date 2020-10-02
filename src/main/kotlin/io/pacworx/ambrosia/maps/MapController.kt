package io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.achievements.AchievementsRepository
import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.buildings.blackmarket.BlackMarketService
import io.pacworx.ambrosia.buildings.merchant.MerchantService
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.MapTileActionException
import io.pacworx.ambrosia.inbox.InboxMessageRepository
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.loot.LootedType
import io.pacworx.ambrosia.oddjobs.OddJobService
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.upgrade.UpgradeService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("map")
class MapController(
    private val mapService: MapService,
    private val playerMapRepository: PlayerMapRepository,
    private val mapRepository: MapRepository,
    private val buildingRepository: BuildingRepository,
    private val resourcesService: ResourcesService,
    private val lootService: LootService,
    private val upgradeService: UpgradeService,
    private val progressRepository: ProgressRepository,
    private val auditLogService: AuditLogService,
    private val oddJobService: OddJobService,
    private val achievementsRepository: AchievementsRepository,
    private val inboxMessageRepository: InboxMessageRepository,
    private val merchantService: MerchantService,
    private val blackMarketService: BlackMarketService
) {

    @GetMapping("{mapId}")
    @Transactional
    fun getPlayerMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerMapResolved {
        return PlayerMapResolved(mapService.getPlayerMap(player, mapId))
    }

    @PostMapping("{mapId}/discover")
    @Transactional
    fun discoverMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        val progress = progressRepository.getOne(player.id)
        val map = mapRepository.findByIdOrNull(mapId) ?: throw EntityNotFoundException(player, "map", mapId)
        val discoveredMap = PlayerMapResolved(mapService.discoverPlayerMap(player, progress, map))
        auditLogService.log(player, "Discover map ${map.name} #${map.id}")
        return PlayerActionResponse(
            progress = progress,
            currentMap = discoveredMap
        )
    }

    @PostMapping("{mapId}/favorite/{favorite}")
    @Transactional
    fun favorite(
        @ModelAttribute("player") player: Player,
        @PathVariable mapId: Long,
        @PathVariable favorite: Boolean
    ): PlayerActionResponse {
        val playerMap = playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)
            ?: throw EntityNotFoundException(player, "playerMap", mapId)
        playerMap.favorite = favorite
        return PlayerActionResponse(
            currentMap = PlayerMapResolved(playerMap)
        )
    }

    @PostMapping("discover")
    @Transactional
    fun discoverTile(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val map = mapService.getPlayerMap(player, request.mapId)
        val tile = map.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discoverable }
            ?: throw MapTileActionException(player, "discover", request.mapId, request.posX, request.posY)
        val resources = resourcesService.spendSteam(player, map.map.discoverySteamCost)
        val achievements = achievementsRepository.getOne(player.id)
        achievements.resourceSpend(ResourceType.STEAM, map.map.discoverySteamCost)
        achievements.mapTilesDiscovered ++
        val oddJobsEffected = oddJobService.mapTileDiscovered(player) +
            oddJobService.resourcesSpend(player, ResourceType.STEAM, map.map.discoverySteamCost)
        mapService.discoverMapTile(player, map, tile)
        auditLogService.log(player, "Discover tile ${tile.posX}x${tile.posY} on map ${map.map.name} #${map.map.id} paying ${map.map.discoverySteamCost} steam")
        return PlayerActionResponse(
            currentMap = PlayerMapResolved(playerMapRepository.save(map)),
            resources = resources,
            achievements = achievements,
            oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() }
        )
    }

    @PostMapping("new_building")
    @Transactional
    fun newBuilding(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val playerMap = mapService.getPlayerMap(player, request.mapId)
        playerMap.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discovered }
            ?: throw MapTileActionException(player, "discover building on", request.mapId, request.posX, request.posY)
        val buildingType = mapRepository.getOne(request.mapId).tiles.find { it.posX == request.posX && it.posY == request.posY }?.takeIf { it.buildingType != null }?.buildingType
            ?: throw MapTileActionException(player, "discover building on", request.mapId, request.posX, request.posY)
        buildingRepository.findByPlayerIdAndType(player.id, buildingType)?.let {
            throw MapTileActionException(player, "discover building on", request.mapId, request.posX, request.posY)
        }
        val building = buildingRepository.save(Building(playerId = player.id, type = buildingType))
        val progress = progressRepository.getOne(player.id)
        upgradeService.applyBuildingLevel(player, building, progress)
        auditLogService.log(player, "Discover building ${buildingType.name} on map ${playerMap.map.name} #${playerMap.map.id}")
        return PlayerActionResponse(
            progress = progress,
            resources = resourcesService.getResources(player),
            buildings = listOf(building),
            merchantItems = building.takeIf { it.type == BuildingType.BAZAAR }?.let { merchantService.getItems(player, progress) },
            blackMarketItems = building.takeIf { it.type == BuildingType.BAZAAR }?.let { blackMarketService.getPurchasableItems(player) }
        )
    }

    @PostMapping("open_chest")
    @Transactional
    fun openChest(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val timestamp = LocalDateTime.now()
        val playerMap = mapService.getPlayerMap(player, request.mapId)
        val tile = playerMap.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discovered}
            ?: throw MapTileActionException(player, "open chest on", request.mapId, request.posX, request.posY)
        if (tile.chestOpened) {
            throw MapTileActionException(player, "open chest on", request.mapId, request.posX, request.posY)
        }
        val lootBoxId = mapRepository.getOne(request.mapId).tiles.find { it.posX == request.posX && it.posY == request.posY }?.takeIf { it.lootBoxId != null }?.lootBoxId
            ?: throw MapTileActionException(player, "open chest on", request.mapId, request.posX, request.posY)
        val achievements = achievementsRepository.getOne(player.id)
        achievements.chestsOpened ++
        val result = lootService.openLootBox(player, lootBoxId, achievements)
        tile.chestOpened = true
        val oddJobsEffected = oddJobService.chestOpened(player) + oddJobService.looted(player, result.items)

        auditLogService.log(player, "Open chest (loot box #${result.lootBoxId}) on map ${playerMap.map.name} #${playerMap.map.id} " +
                "looting ${result.items.joinToString { it.auditLog() }}"
        )

        return PlayerActionResponse(
            currentMap = PlayerMapResolved(playerMap),
            achievements = achievements,
            progress = if (result.items.any { it.progress != null }) { progressRepository.getOne(player.id) } else { null },
            resources = resourcesService.getResources(player),
            heroes = result.items.filter { it.hero != null }.map { it.hero!! }.takeIf { it.isNotEmpty() },
            gears = result.items.filter { it.gear != null }.map { it.gear!! }.takeIf{ it.isNotEmpty() },
            jewelries = result.items.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicles = result.items.filter { it.vehicle != null }.map { it.vehicle!! }.takeIf { it.isNotEmpty() },
            vehicleParts = result.items.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            looted = Looted(LootedType.CHEST, result.items.map { lootService.asLootedItem(it) }),
            oddJobs = oddJobsEffected.takeIf { it.isNotEmpty() },
            inboxMessages = inboxMessageRepository.findAllByPlayerIdAndSendTimestampIsAfter(player.id, timestamp.minusSeconds(1))
        )
    }

    @PostMapping("{mapId}/current")
    @Transactional
    fun setCurrentMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        return mapService.getPlayerMap(player, mapId).let {
            mapService.checkMapForUpdates(player, it)
            it.lastVisited = LocalDateTime.now()
            val progress = progressRepository.getOne(player.id)
            progress.currentMapId = mapId
            auditLogService.log(player, "Set map $mapId as current map")
            PlayerActionResponse(
                progress = progress,
                currentMap = PlayerMapResolved(it)
            )
        }
    }

}

data class TileRequest(
    val mapId: Long,
    val posX: Int,
    val posY: Int
)
