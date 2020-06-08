package io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.exceptions.GeneralException
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/map")
class AdminMapController(
    private val mapRepository: MapRepository,
    private val auditLogService: AuditLogService
) {

    @GetMapping
    fun getAllMaps(): List<Map> = mapRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createMap(@ModelAttribute("player") player: Player,
                  @RequestBody @Valid request: NewMapRequest): Map {
        val map = Map(
            name = request.name,
            minX = 1,
            maxX = request.width,
            minY = 1,
            maxY = request.height,
            background = MapBackground.BLUE_SKY
        )
        map.tiles = (map.minX..map.maxX).map { x ->
            (map.minY..map.maxY).map { y ->
                MapTile(type = MapTileType.NONE, posX = x, posY = y)
            }
        }.flatten()
        return mapRepository.save(map)
            .also {
                auditLogService.log(player,"Create map ${it.name} #${it.id}", adminAction = true)
            }
    }

    @PutMapping("{id}")
    @Transactional
    fun updateMap(@ModelAttribute("player") player: Player,
                  @PathVariable id: Long,
                  @RequestBody @Valid map: Map): Map {
        if (map.tiles.none { it.alwaysRevealed }) {
            throw GeneralException(player, "Invalid map", "There must be at least one tile that is always revealed")
        }
        if (map.tiles.any { (it.posX == map.minX || it.posX == map.maxX) && it.type != MapTileType.NONE } ||
            map.tiles.any { (it.posY == map.minY || it.posY == map.maxY) && it.type != MapTileType.NONE }) {
            throw GeneralException(player, "Invalid map", "Border tiles must be of type NONE")
        }
        map.tiles.filter { it.structure != null }.forEach {
            if (it.structure!!.type == MapTileStructureType.PORTAL) {
                if (it.buildingType != null) {
                    throw GeneralException(player, "Invalid map", "Tile ${it.posX}x${it.posY} has a portal structure but a building assigned")
                }
                if (it.portalToMapId == null) {
                    throw GeneralException(player, "Invalid map", "Tile ${it.posX}x${it.posY} has a portal structure but no map portal to assigned")
                }
            } else if (it.structure.type == MapTileStructureType.BUILDING) {
                if (it.portalToMapId != null) {
                    throw GeneralException(player, "Invalid map", "Tile ${it.posX}x${it.posY} has a building structure but a map portal to assigned")
                }
                if (it.buildingType == null) {
                    throw GeneralException(player, "Invalid map", "Tile ${it.posX}x${it.posY} has a building structure but no building assigned")
                } else if (it.buildingType.name != it.structure.name) {
                    throw GeneralException(player, "Invalid map", "Tile ${it.posX}x${it.posY} has a different building structure than building assigned")
                }
            }
        }
        if (map.startingMap) {
            mapRepository.markStartingMap(map.id)
        }
        map.lastModified = LocalDateTime.now()
        return mapRepository.save(map)
            .also {
                auditLogService.log(player, "Update map ${it.name} #${it.id}", adminAction = true)
            }
    }
}

data class NewMapRequest(
    val name: String,
    @Min(1) val width: Int,
    @Min(1) val height: Int
)

data class ResetMapRequest(
    val discovered: Boolean,
    val fights: Boolean,
    val chests: Boolean
)
