package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Color
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
import org.springframework.stereotype.Service
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import kotlin.math.abs

@Service
class MapService(val playerRepository: PlayerRepository,
                 val playerMapRepository: PlayerMapRepository,
                 val mapRepository: MapRepository) {

    fun getCurrentPlayerMap(player: Player): PlayerMapResolved {
        return player.currentMapId?.let {PlayerMapResolved(playerMapRepository.getByPlayerIdAndMapId(player.id, it)!!)}
            ?: PlayerMapResolved(discoverPlayerMap(player, mapRepository.getByStartingMapTrue()))
    }

    fun discoverPlayerMap(player: Player, map: Map): PlayerMap {
        val playerMap = PlayerMap(playerId = player.id, map = map)
        playerMap.playerTiles = map.tiles.filter { it.type != MapTileType.NONE }.map {
            PlayerMapTile(posX = it.posX, posY = it.posY)
        }
        map.tiles.filter {
            when(player.color) {
                Color.RED -> it.redAlwaysRevealed
                Color.GREEN -> it.greenAlwaysRevealed
                Color.BLUE -> it.blueAlwaysRevealed
                else -> false
            }
        }.forEach { discoverMapTile(playerMap, it) }

        player.currentMapId = map.id
        playerRepository.save(player)
        return playerMapRepository.save(playerMap)
    }

    fun discoverMapTile(playerMap: PlayerMap, tile: MapTile) {
        val playerMapTile = playerMap.playerTiles.find { it.posX == tile.posX && it.posY == tile.posY }
            ?: throw RuntimeException("Cannot find playerMapTile ${tile.posX}/${tile.posY} on map ${playerMap.map.id} for player ${playerMap.playerId}")
        discoverMapTile(playerMap, playerMapTile, tile)
    }

    fun discoverMapTile(playerMap: PlayerMap, playerMapTile: PlayerMapTile) {
        val tile = playerMap.map.tiles.find { it.type != MapTileType.NONE && it.posX == playerMapTile.posX && it.posY == playerMapTile.posY }
            ?: throw RuntimeException("Cannot find mapTile ${playerMapTile.posX}/${playerMapTile.posY} on map ${playerMap.map.id}")
        discoverMapTile(playerMap, playerMapTile, tile)
    }

    fun discoverMapTile(playerMap: PlayerMap, playerMapTile: PlayerMapTile, tile: MapTile) {
        playerMapTile.discovered = true
        playerMapTile.discoverable = false
        if (tile.dungeonId == null) {
            undiscoveredNeighbors(playerMap.playerTiles, playerMapTile.posX, playerMapTile.posY).forEach {
                it.discoverable = true
            }
        }
    }

    fun checkMapForUpdates(playerMap: PlayerMap) {
        // remove playerMapTiles that now doesn't exist anymore or have type NONE
        // create undiscovered playerMapTiles for all tiles with type other than NONE
        playerMap.playerTiles = playerMap.playerTiles
            .filter { tile -> playerMap.map.tiles.any { tile.posX == it.posX && tile.posY == it.posY && it.type != MapTileType.NONE } } +
                playerMap.map.tiles.filter { tile -> tile.type != MapTileType.NONE && playerMap.playerTiles.none { tile.posX == it.posX && tile.posY == it.posY } }.map { PlayerMapTile(posX = it.posX, posY = it.posY) }

        // set all tiles to not discoverable and recalculate discoverable tiles
        playerMap.playerTiles.forEach { it.discoverable = false }
        playerMap.playerTiles.filter { it.discovered }.forEach { discoverMapTile(playerMap, it) }
    }

    private fun undiscoveredNeighbors(tiles: List<PlayerMapTile>, x: Int, y: Int): List<PlayerMapTile> {
        val even = y % 2 == 0
        val evenDiff = if (even) { 1 } else { -1 }
        return tiles.filter {
            !it.discovered && !it.discoverable
        }.filter {
            when {
                it.posY == y -> abs(it.posX - x) == 1
                abs(it.posY - y) == 1 -> it.posX == x || it.posX + evenDiff == x
                else -> false
            }
        }
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
            PlayerMapTileResolved(
                tile,
                playerMap.playerTiles.find { tile.posX == it.posX && tile.posY == it.posY })
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
        playerTile?.discovered?.takeIf { it && (tile.fightRepeatable || !playerTile.victoriousFight) }?.let { tile.fightIcon },
        playerTile?.discovered?.takeIf { it && (tile.fightRepeatable || !playerTile.victoriousFight) }?.let { tile.dungeonId },
        playerTile?.discovered?.takeIf { it }?.let { tile.fightRepeatable },
        playerTile?.discovered?.takeIf { it }?.let { tile.portalToMapId }
    )
}
