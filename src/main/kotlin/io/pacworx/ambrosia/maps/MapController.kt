package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

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
                    private val playerRepository: PlayerRepository) {

    @PostMapping("{mapId}/discover")
    @Transactional
    fun discoverMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        return PlayerActionResponse(currentMap = PlayerMapResolved(mapService.discoverPlayerMap(player, mapRepository.getOne(mapId))))
    }

    @PostMapping("discover")
    @Transactional
    fun discoverTile(@ModelAttribute("player") player: Player, @RequestBody request: DiscoverRequest): PlayerActionResponse {
        val map = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
            ?: throw RuntimeException("Map ${request.mapId} is unknown to player ${player.id}")
        val tile = map.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discoverable }
            ?: throw RuntimeException("Cannot discover tile ${request.posX}/${request.posY} on map ${request.mapId} for player ${player.id}")
        mapService.discoverMapTile(map, tile)
        return PlayerActionResponse(currentMap = PlayerMapResolved(playerMapRepository.save(map)))
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

data class DiscoverRequest(
    val mapId: Long,
    val posX: Int,
    val posY: Int
)
