package io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.MapTileActionException
import io.pacworx.ambrosia.loot.LootService
import io.pacworx.ambrosia.loot.Looted
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.progress.ProgressRepository
import io.pacworx.ambrosia.resources.ResourcesService
import io.pacworx.ambrosia.upgrade.UpgradeService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("map")
class MapController(private val mapService: MapService,
                    private val playerMapRepository: PlayerMapRepository,
                    private val mapRepository: MapRepository,
                    private val playerRepository: PlayerRepository,
                    private val buildingRepository: BuildingRepository,
                    private val resourcesService: ResourcesService,
                    private val lootService: LootService,
                    private val upgradeService: UpgradeService,
                    private val progressRepository: ProgressRepository) {

    @GetMapping("{mapId}")
    fun getPlayerMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerMapResolved {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)?.let { PlayerMapResolved(it) }
            ?: throw EntityNotFoundException(player, "map", mapId)
    }

    @PostMapping("{mapId}/discover")
    @Transactional
    fun discoverMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        return PlayerActionResponse(currentMap = PlayerMapResolved(mapService.discoverPlayerMap(player, mapRepository.getOne(mapId))))
    }

    @PostMapping("discover")
    @Transactional
    fun discoverTile(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val map = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
            ?: throw EntityNotFoundException(player, "map", request.mapId)
        val tile = map.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discoverable }
            ?: throw MapTileActionException(player, "discover", request.mapId, request.posX, request.posY)
        val resources = resourcesService.spendSteam(player, map.map.discoverySteamCost)
        mapService.discoverMapTile(map, tile)
        return PlayerActionResponse(currentMap = PlayerMapResolved(playerMapRepository.save(map)), resources = resources)
    }

    @PostMapping("new_building")
    @Transactional
    fun newBuilding(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val playerMap = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
            ?: throw EntityNotFoundException(player, "map", request.mapId)
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
        return PlayerActionResponse(
            progress = progress,
            resources = resourcesService.getResources(player),
            buildings = listOf(building)
        )
    }

    @PostMapping("open_chest")
    @Transactional
    fun openChest(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val playerMap = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
            ?: throw EntityNotFoundException(player, "map", request.mapId)
        val tile = playerMap.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discovered}
            ?: throw MapTileActionException(player, "open chest on", request.mapId, request.posX, request.posY)
        if (tile.chestOpened) {
            throw MapTileActionException(player, "open chest on", request.mapId, request.posX, request.posY)
        }
        val lootBoxId = mapRepository.getOne(request.mapId).tiles.find { it.posX == request.posX && it.posY == request.posY }?.takeIf { it.lootBoxId != null }?.lootBoxId
            ?: throw MapTileActionException(player, "open chest on", request.mapId, request.posX, request.posY)
        val result = lootService.openLootBox(player, lootBoxId)
        tile.chestOpened = true

        return PlayerActionResponse(
            currentMap = PlayerMapResolved(playerMap),
            resources = resourcesService.getResources(player),
            heroes = result.items.filter { it.hero != null }.map { it.hero!! }.takeIf { it.isNotEmpty() },
            gears = result.items.filter { it.gear != null }.map { it.gear!! }.takeIf{ it.isNotEmpty() },
            jewelries = result.items.filter { it.jewelry != null }.map { it.jewelry!! }.takeIf { it.isNotEmpty() },
            vehicles = result.items.filter { it.vehicle != null }.map { it.vehicle!! }.takeIf { it.isNotEmpty() },
            vehicleParts = result.items.filter { it.vehiclePart != null }.map { it.vehiclePart!! }.takeIf { it.isNotEmpty() },
            looted = result.items.map { lootService.asLooted(it) }
        )
    }

    @PostMapping("{mapId}/current")
    @Transactional
    fun setCurrentMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)?.let {
            mapService.checkMapForUpdates(it)
            player.currentMapId = mapId
            PlayerActionResponse(player = playerRepository.save(player), currentMap = PlayerMapResolved(it))
        } ?: throw EntityNotFoundException(player, "map", mapId)
    }

}

data class TileRequest(
    val mapId: Long,
    val posX: Int,
    val posY: Int
)
