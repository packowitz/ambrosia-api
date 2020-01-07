package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
import org.springframework.web.bind.annotation.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("map")
class MapController(private val mapService: MapService,
                    private val playerMapRepository: PlayerMapRepository,
                    private val mapRepository: MapRepository,
                    private val playerRepository: PlayerRepository,
                    private val simplePlayerMapRepository: SimplePlayerMapRepository) {

    @GetMapping
    @Transactional
    fun getAllPlayerMaps(@ModelAttribute("player") player: Player): List<PlayerMapResolved> {
        val playerMaps = playerMapRepository.getAllByPlayerId(player.id).takeIf { it.isNotEmpty() }
            ?: listOf(mapService.discoverPlayerMap(player, mapRepository.getByStartingMapTrue()))
        playerMaps.filter { it.mapCheckedTimestamp.isBefore(it.map.lastModified) }.forEach {
            mapService.checkMapForUpdates(it)
        }
        return playerMaps.map { PlayerMapResolved(it) }
    }

    @GetMapping("simple")
    @Transactional
    fun getSimplePlayerMaps(@ModelAttribute("player") player: Player): List<PlayerMapResolved> {
        return simplePlayerMapRepository.findAllByPlayerId(player.id)
    }

    @GetMapping("current")
    @Transactional
    fun getStartingMap(@ModelAttribute("player") player: Player): PlayerMapResolved {
        return player.currentMapId?.let {PlayerMapResolved(playerMapRepository.getByPlayerIdAndMapId(player.id, it)!!)}
            ?: PlayerMapResolved(mapService.discoverPlayerMap(player, mapRepository.getByStartingMapTrue()))
    }

    @GetMapping("{mapId}")
    fun getPlayerMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerMapResolved {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)?.let { PlayerMapResolved(it) }
            ?: throw RuntimeException("Map $mapId is unknown to player ${player.id}")
    }

    @PostMapping("{mapId}/discover")
    @Transactional
    fun discoverMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerMapResolved {
        return PlayerMapResolved(mapService.discoverPlayerMap(player, mapRepository.getOne(mapId)))
    }

    @PostMapping("discover")
    @Transactional
    fun discoverTile(@ModelAttribute("player") player: Player, @RequestBody request: DiscoverRequest): PlayerMapResolved {
        val map = playerMapRepository.getByPlayerIdAndMapId(player.id, request.mapId)
            ?: throw RuntimeException("Map ${request.mapId} is unknown to player ${player.id}")
        val tile = map.playerTiles.find { it.posX == request.posX && it.posY == request.posY && it.discoverable }
            ?: throw RuntimeException("Cannot discover tile ${request.posX}/${request.posY} on map ${request.mapId} for player ${player.id}")
        mapService.discoverMapTile(map, tile)
        return PlayerMapResolved(playerMapRepository.save(map))
    }

    @PostMapping("{mapId}/current")
    @Transactional
    fun setCurrentMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long): PlayerActionResponse {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)?.let {
            player.currentMapId = it.id
            PlayerActionResponse(player = playerRepository.save(player))
        } ?: throw RuntimeException("Map $mapId is unknown to player ${player.id}")
    }

}

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlayerMapResolved(
    @Id @JsonIgnore val id: String,
    val mapId: Long,
    val name: String,
    val background: String,
    @Column(name = "min_x") val minX: Int,
    @Column(name = "max_x") val maxX: Int,
    @Column(name = "min_y") val minY: Int,
    @Column(name = "max_y") val maxY: Int,
    @Transient val tiles: List<PlayerMapTileResolved>? = null
) {
    constructor(playerMap: PlayerMap): this(
        "${playerMap.playerId}_${playerMap.map.id}",
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
    val fightId: Long? = null,
    val fightRepeatable: Boolean? = null,
    val portalToMapId: Long? = null
) {
    constructor(tile: MapTile, playerTile: PlayerMapTile?): this(
        tile.posX,
        tile.posY,
        tile.type,
        playerTile?.discovered ?: false,
        playerTile?.discoverable ?: false,
        playerTile?.discovered?.takeIf { it }?.let { tile.structure },
        playerTile?.discovered?.takeIf { it && tile.fightRepeatable || playerTile.victoriousFight }?.let { tile.fightIcon },
        playerTile?.discovered?.takeIf { it && tile.fightRepeatable || playerTile.victoriousFight }?.let { tile.dungeonId },
        playerTile?.discovered?.takeIf { it }?.let { tile.fightRepeatable },
        playerTile?.discovered?.takeIf { it }?.let { tile.portalToMapId }
    )
}

data class DiscoverRequest(
    val mapId: Long,
    val posX: Int,
    val posY: Int
)
