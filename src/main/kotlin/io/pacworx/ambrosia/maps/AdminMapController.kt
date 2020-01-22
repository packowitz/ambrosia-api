package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/map")
class AdminMapController(private val mapRepository: MapRepository) {

    @GetMapping
    fun getAllMaps(): List<Map> = mapRepository.findAll()

    @PostMapping("new")
    @Transactional
    fun createMap(@RequestBody @Valid request: NewMapRequest): Map {
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
    }

    @PutMapping("{id}")
    @Transactional
    fun updateMap(@PathVariable id: Long, @RequestBody @Valid map: Map): Map {
        if (map.tiles.none { it.blueAlwaysRevealed } ||
            map.tiles.none { it.greenAlwaysRevealed } ||
            map.tiles.none { it.redAlwaysRevealed }) {
            throw RuntimeException("There must be at least one tile that is revealed for each color")
        }
        if (map.tiles.any { (it.posX == map.minX || it.posX == map.maxX) && it.type != MapTileType.NONE } ||
            map.tiles.any { (it.posY == map.minY || it.posY == map.maxY) && it.type != MapTileType.NONE }) {
            throw RuntimeException("Border tiles must be of type NONE")
        }
        map.tiles.filter { it.structure != null }.forEach {
            if (it.structure!!.type == MapTileStructureType.PORTAL) {
                if (it.buildingType != null) {
                    throw RuntimeException("Tile ${it.posX}x${it.posY} has a portal structure but a building assigned")
                }
                if (it.portalToMapId == null) {
                    throw RuntimeException("Tile ${it.posX}x${it.posY} has a portal structure but no map portal to assigned")
                }
            } else if (it.structure.type == MapTileStructureType.BUILDING) {
                if (it.portalToMapId != null) {
                    throw RuntimeException("Tile ${it.posX}x${it.posY} has a building structure but a map portal to assigned")
                }
                if (it.buildingType == null) {
                    throw RuntimeException("Tile ${it.posX}x${it.posY} has a building structure but building assigned")
                } else if (it.buildingType.name != it.structure.name) {
                    throw RuntimeException("Tile ${it.posX}x${it.posY} has a different building structure than building assigned")
                }
            }
        }
        if (map.startingMap) {
            mapRepository.markStartingMap(map.id)
        }
        return mapRepository.save(map)
    }
}

data class NewMapRequest(
    val name: String,
    @Min(1) val width: Int,
    @Min(1) val height: Int
)