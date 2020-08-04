package io.pacworx.ambrosia.maps

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.progress.Progress
import io.pacworx.ambrosia.story.StoryTrigger
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.*
import javax.transaction.Transactional
import kotlin.math.abs

@Service
class MapService(
    val playerMapRepository: PlayerMapRepository,
    val mapRepository: MapRepository,
    val simplePlayerMapRepository: SimplePlayerMapRepository
) {
    private val log = KotlinLogging.logger {}

    fun getPlayerMaps(player: Player): List<PlayerMapResolved> {
        return simplePlayerMapRepository.findAllByPlayerId(player.id)
    }

    @Transactional
    fun getCurrentPlayerMap(player: Player, progress: Progress): PlayerMapResolved {
        return progress.currentMapId?.let {
            val playerMap = getPlayerMap(player, it)
            playerMap.lastVisited = LocalDateTime.now()
            checkMapForUpdates(player, playerMap)
            PlayerMapResolved(playerMap)
        } ?: PlayerMapResolved(discoverPlayerMap(player, progress, mapRepository.getByStartingMapTrue()))
    }

    fun getPlayerMap(player: Player, mapId: Long): PlayerMap {
        return playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)
            ?.also { checkForReset(player, it) }
            ?: throw EntityNotFoundException(player, "map", mapId)
    }

    fun discoverPlayerMap(player: Player, progress: Progress, map: Map): PlayerMap {
        val playerMap = PlayerMap(playerId = player.id, map = map)
        playerMap.playerTiles = map.tiles.filter { it.type != MapTileType.NONE }.map {
            PlayerMapTile(posX = it.posX, posY = it.posY)
        }.toMutableList()
        map.tiles.filter { it.alwaysRevealed }.forEach { discoverMapTile(player, playerMap, it) }

        progress.currentMapId = map.id
        return playerMapRepository.save(playerMap)
    }

    fun discoverMapTile(player: Player, playerMap: PlayerMap, tile: MapTile) {
        val playerMapTile = playerMap.playerTiles.find { it.posX == tile.posX && it.posY == tile.posY }
            ?: throw GeneralException(player, "player map tile", "Cannot find playerMapTile ${tile.posX}/${tile.posY} on map ${playerMap.map.id} for player ${playerMap.playerId}")
        discoverMapTile(player, playerMap, playerMapTile, tile)
    }

    fun discoverMapTile(player: Player, playerMap: PlayerMap, playerMapTile: PlayerMapTile) {
        val tile = playerMap.map.tiles.find { it.type != MapTileType.NONE && it.posX == playerMapTile.posX && it.posY == playerMapTile.posY }
            ?: throw GeneralException(player, "player map tile", "Cannot find mapTile ${playerMapTile.posX}/${playerMapTile.posY} on map ${playerMap.map.id}")
        discoverMapTile(player, playerMap, playerMapTile, tile)
    }

    fun discoverMapTile(player: Player, playerMap: PlayerMap, playerMapTile: PlayerMapTile, tile: MapTile) {
        playerMapTile.discovered = true
        playerMapTile.discoverable = false
        if (tile.fightId == null || playerMapTile.victoriousFight) {
            undiscoveredNeighbors(playerMap.playerTiles, playerMapTile.posX, playerMapTile.posY).forEach {
                it.discoverable = true
            }
        }
        checkMapForUpdates(player, playerMap)
    }

    fun victoriousFight(player: Player, mapId: Long, posX: Int, posY: Int): PlayerMapResolved? {
        log.info("player ${player.id} was victorious on map $mapId $posX/$posY")
        val playerMap = getPlayerMap(player, mapId)
        val playerTile = playerMap.playerTiles.find { it.posX == posX && it.posY == posY }!!
        return if (!playerTile.victoriousFight) {
            if (playerTile.discovered) {
                playerTile.victoriousFight = true
                discoverMapTile(player, playerMap, playerTile)
            }
            PlayerMapResolved(playerMap)
        } else {
            null
        }
    }

    fun checkMapForUpdates(player: Player, playerMap: PlayerMap) {
        if (playerMap.map.lastModified.isAfter(playerMap.mapCheckedTimestamp)) {
            log.info("Checking map ${playerMap.map.id} for updates for player ${playerMap.playerId}")
            playerMap.mapCheckedTimestamp = LocalDateTime.now()

            // remove playerMapTiles that now doesn't exist anymore or have type NONE
            val removedTiles = playerMap.playerTiles.filter { tile ->
                playerMap.map.tiles.none { tile.posX == it.posX && tile.posY == it.posY && it.type != MapTileType.NONE }
            }
            if (removedTiles.isNotEmpty()) {
                log.info("Removing ${removedTiles.size} tiles from map ${playerMap.map.id} for player ${playerMap.playerId}")
                playerMap.playerTiles.removeAll(removedTiles)
            }

            // create undiscovered playerMapTiles for all tiles with type other than NONE
            val addedTiles = playerMap.map.tiles.filter { tile ->
                tile.type != MapTileType.NONE && playerMap.playerTiles.none { tile.posX == it.posX && tile.posY == it.posY }
            }.map { PlayerMapTile(posX = it.posX, posY = it.posY) }
            if (addedTiles.isNotEmpty()) {
                log.info("Adding ${removedTiles.size} tiles to map ${playerMap.map.id} for player ${playerMap.playerId}")
                playerMap.playerTiles.addAll(addedTiles)
            }

            // set all tiles to not discoverable and recalculate discoverable tiles
            playerMap.playerTiles.forEach { it.discoverable = false }
            playerMap.playerTiles.filter { it.discovered }.forEach { discoverMapTile(player, playerMap, it) }
        }
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

    private fun checkForReset(player: Player, playerMap: PlayerMap) {
        if (playerMap.map.resetIntervalHours != null) {
            val resetTime = if (LocalDateTime.now().isAfter(playerMap.map.intervalTo)) {
                playerMap.map.intervalTo
            } else {
                playerMap.map.intervalFrom
            }
            if (playerMap.created.isEqual(resetTime) || playerMap.created.isBefore(resetTime)) {
                playerMap.created = LocalDateTime.now()
                playerMap.playerTiles.forEach { playerTile ->
                    playerTile.discovered = false
                    playerTile.chestOpened = false
                    playerTile.discoverable = false
                    playerTile.victoriousFight = false
                }
                playerMap.map.tiles.filter { it.alwaysRevealed }.forEach {
                    discoverMapTile(player, playerMap, it)
                }
            }
        }
    }

}

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlayerMapResolved(
    @Id
    @JsonIgnore
    val id: String,
    val mapId: Long,
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: MapType,
    val background: String,
    val discoverySteamCost: Int,
    @Enumerated(EnumType.STRING)
    val storyTrigger: StoryTrigger?,
    val favorite: Boolean,
    @Column(name = "min_x")
    val minX: Int,
    @Column(name = "max_x")
    val maxX: Int,
    @Column(name = "min_y")
    val minY: Int,
    @Column(name = "max_y")
    val maxY: Int,
    @JsonIgnore
    val intervalFrom: LocalDateTime? = null,
    @JsonIgnore
    val intervalTo: LocalDateTime? = null,
    @JsonIgnore
    val resetIntervalHours: Int? = null,
    @JsonIgnore
    val lastVisited: LocalDateTime,
    @Transient
    val tiles: List<PlayerMapTileResolved>? = null
) {
    constructor(playerMap: PlayerMap): this(
        "${playerMap.playerId}_${playerMap.map.id}",
        playerMap.map.id,
        playerMap.map.name,
        playerMap.map.type,
        playerMap.map.background.name,
        playerMap.map.discoverySteamCost,
        playerMap.map.storyTrigger,
        playerMap.favorite,
        playerMap.map.minX,
        playerMap.map.maxX,
        playerMap.map.minY,
        playerMap.map.maxY,
        playerMap.map.intervalFrom,
        playerMap.map.intervalTo,
        playerMap.map.resetIntervalHours,
        playerMap.lastVisited,
        playerMap.map.tiles.filter { it.type != MapTileType.NONE }.map { tile ->
            PlayerMapTileResolved(
                tile,
                playerMap.playerTiles.find { tile.posX == it.posX && tile.posY == it.posY })
        })

    fun getSecondsToReset(): Long? {
        return resetIntervalHours?.let {
            val now = LocalDateTime.now()
            val resetTime = if (now.isAfter(intervalTo)) {
                intervalTo?.plusHours(resetIntervalHours.toLong())
            } else {
                intervalTo
            }
            now.until(resetTime, ChronoUnit.SECONDS) + 1
        }
    }

    fun isUnvisited(): Boolean {
        if (resetIntervalHours != null) {
            val resetTime = if (LocalDateTime.now().isAfter(intervalTo)) {
                intervalTo
            } else {
                intervalFrom
            }
            return resetTime!!.isAfter(lastVisited)
        }
        return false
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlayerMapTileResolved(
    val posX: Int,
    val posY: Int,
    val type: MapTileType,
    val discovered: Boolean,
    val discoverable: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING) val structure: MapTileStructure? = null,
    val fightIcon: FightIcon? = null,
    val fightId: Long? = null,
    val fightRepeatable: Boolean? = null,
    val victoriousFight: Boolean? = null,
    val portalToMapId: Long? = null,
    val buildingType: BuildingType? = null
) {
    constructor(tile: MapTile, playerTile: PlayerMapTile?): this(
        tile.posX,
        tile.posY,
        tile.type,
        playerTile?.discovered ?: false,
        playerTile?.discoverable ?: false,
        playerTile?.takeIf { it.discovered && (tile.lootBoxId == null || !it.chestOpened) }?.let { tile.structure },
        playerTile?.takeIf { it.discovered && (tile.fightRepeatable || !it.victoriousFight) }?.let { tile.fightIcon },
        playerTile?.takeIf { it.discovered && (tile.fightRepeatable || !it.victoriousFight) }?.let { tile.fightId },
        playerTile?.takeIf { it.discovered }?.let { tile.fightRepeatable },
        playerTile?.takeIf { it.discovered }?.victoriousFight,
        playerTile?.takeIf { it.discovered }?.let { tile.portalToMapId },
        playerTile?.takeIf { it.discovered }?.let { tile.buildingType }
    )
}
