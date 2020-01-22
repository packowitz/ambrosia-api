package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.buildings.Building
import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
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
@RequestMapping("map")
class MapController(private val mapService: MapService,
                    private val playerMapRepository: PlayerMapRepository,
                    private val mapRepository: MapRepository,
                    private val playerRepository: PlayerRepository,
                    private val buildingRepository: BuildingRepository) {

    @GetMapping("{mapId}")
    fun getPlayerMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerMapResolved {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)?.let { PlayerMapResolved(it) }
            ?: throw RuntimeException("Map $mapId is unknown to player ${player.id}")
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
            ?: throw RuntimeException("Map ${request.mapId} is unknown to player ${player.id}")
        val tile = map.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discoverable }
            ?: throw RuntimeException("Cannot discover tile ${request.posX}/${request.posY} on map ${request.mapId} for player ${player.id}")
        mapService.discoverMapTile(map, tile)
        return PlayerActionResponse(currentMap = PlayerMapResolved(playerMapRepository.save(map)))
    }

    @PostMapping("new_building")
    @Transactional
    fun newBuilding(@ModelAttribute("player") player: Player, @RequestBody request: TileRequest): PlayerActionResponse {
        val playerMap = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
            ?: throw RuntimeException("Map ${request.mapId} is unknown to player ${player.id}")
        playerMap.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discovered }
            ?: throw RuntimeException("Tile ${request.posX}/${request.posY} on map ${request.mapId} is not discovered by player ${player.id}")
        val buildingType = mapRepository.getOne(request.mapId).tiles.find { it.posX == request.posX && it.posY == request.posY }?.takeIf { it.buildingType != null }?.buildingType
            ?: throw RuntimeException("Tile ${request.posX}/${request.posY} on map ${request.mapId} has no building to enter")
        buildingRepository.findByPlayerIdAndType(player.id, buildingType)?.let {
            throw RuntimeException("Player ${player.id} has building ${buildingType.name} already discovered")
        }
        val building = buildingRepository.save(Building(playerId = player.id, type = buildingType))
        return PlayerActionResponse(buildings = listOf(building))
    }

    @PostMapping("{mapId}/current")
    @Transactional
    fun setCurrentMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)?.let {
            player.currentMapId = mapId
            PlayerActionResponse(player = playerRepository.save(player), currentMap = PlayerMapResolved(it))
        } ?: throw RuntimeException("Map $mapId is unknown to player ${player.id}")
    }

}

data class TileRequest(
    val mapId: Long,
    val posX: Int,
    val posY: Int
)
