package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("map")
class MapController(private val mapService: MapService,
                    private val playerMapRepository: PlayerMapRepository,
                    private val mapRepository: MapRepository) {

    @GetMapping
    @Transactional
    fun getAllPlayerMaps(@ModelAttribute("player") player: Player): List<PlayerMapResolved> {
        val playerMaps = playerMapRepository.getAllByPlayerId(player.id).map {
            PlayerMapResolved(it)
        }
        if (playerMaps.isEmpty()) {
            return listOf(PlayerMapResolved(mapService.discoverPlayerMap(player, mapRepository.getByStartingMapTrue())))
        }
        return playerMaps
    }

    @PostMapping("discover")
    @Transactional
    fun discoverTile(@ModelAttribute("player") player: Player, @RequestBody request: DiscoverRequest): PlayerMapResolved {
        val map = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
        val tile = map.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discoverable }
            ?: throw RuntimeException("Cannot discover tile ${request.posX}/${request.posY} on map ${request.mapId} for player ${player.id}")
        mapService.discoverMapTile(map, tile)
        return PlayerMapResolved(playerMapRepository.save(map))
    }

}

data class PlayerMapResolved(
    val mapId: Long,
    val name: String,
    val background: String,
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
    val tiles: List<PlayerMapTileResolved> = mutableListOf()
) {
    constructor(playerMap: PlayerMap): this(
        playerMap.map.id,
        playerMap.map.name,
        playerMap.map.background.name,
        playerMap.map.minX,
        playerMap.map.maxX,
        playerMap.map.minY,
        playerMap.map.maxY,
        playerMap.map.tiles.map { tile ->
            PlayerMapTileResolved(tile, playerMap.playerTiles.find { tile.posX == it.posX && tile.posY == it.posY })
        })
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlayerMapTileResolved(
    val posX: Int,
    val posY: Int,
    val type: MapTileType,
    val discovered: Boolean,
    val discoverable: Boolean,
    val structure: MapTileStructure? = null,
    val fightIcon: FightIcon? = null,
    val fightRepeatable: Boolean? = null,
    val portalToMapId: Long? = null
) {
    constructor(tile: MapTile, playerTile: PlayerMapTile?): this(
        tile.posX,
        tile.posY,
        tile.type,
        playerTile?.discovered ?: false,
        playerTile?.discoverable ?: false,
        playerTile?.discovered?.takeIf { it }.let { tile.structure },
        playerTile?.discovered?.takeIf { it }.let { tile.fightIcon },
        playerTile?.discovered?.takeIf { it }.let { tile.fightRepeatable },
        playerTile?.discovered?.takeIf { it }.let { tile.portalToMapId }
    )
}

data class DiscoverRequest(
    val mapId: Long,
    val posX: Int,
    val posY: Int
)