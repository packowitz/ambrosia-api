package io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("tester/map")
class BetaTesterMapController(
    private val playerMapRepository: PlayerMapRepository,
    private val mapService: MapService,
    private val auditLogService: AuditLogService
) {

    @PostMapping("{mapId}/reset")
    @Transactional
    fun resetMap(@ModelAttribute("player") player: Player, @PathVariable mapId: Long, @RequestBody request: ResetMapRequest): PlayerActionResponse {
        val playerMap = playerMapRepository.getByPlayerIdAndMapId(player.id, mapId)
            ?: throw EntityNotFoundException(player, "player map", mapId)
        playerMap.playerTiles.forEach {
            it.discoverable = if (request.discovered) false else it.discoverable
            it.discovered = if (request.discovered) false else it.discovered
            it.victoriousFight = if (request.fights) false else it.victoriousFight
            it.chestOpened = if (request.chests) false else it.chestOpened
        }
        if (request.discovered) {
            playerMap.map.tiles.filter { it.alwaysRevealed }.forEach { mapService.discoverMapTile(player, playerMap, it) }
        }
        auditLogService.log(player, "Reset map $mapId reset discovery ${request.discovered} fights ${request.fights} chests ${request.chests}", betaTesterAction = true)
        return PlayerActionResponse(currentMap = PlayerMapResolved(playerMap))
    }
}
