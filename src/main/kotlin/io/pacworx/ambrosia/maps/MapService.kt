package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Color
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.PlayerRepository
import org.springframework.stereotype.Service

@Service
class MapService(val playerRepository: PlayerRepository,
                 val playerMapRepository: PlayerMapRepository) {

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

    fun undiscoveredNeighbors(tiles: List<PlayerMapTile>, x: Int, y: Int): List<PlayerMapTile> {
        val even = y % 2 == 0
        val evenDiff = if (even) { 1 } else { -1 }
        return tiles.filter {
            !it.discovered && !it.discoverable
        }.filter {
            when {
                it.posY == y -> Math.abs(it.posX - x) == 1
                Math.abs(it.posY - y) == 1 -> it.posX == x || it.posX + evenDiff == x
                else -> false
            }
        }
    }

}