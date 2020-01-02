package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.Map
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapTile
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapTileType
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/map")
class AdminMapController(private val mapRepository: MapRepository) {

    @PostMapping("new")
    @Transactional
    fun createMap(@RequestBody @Valid request: NewMapRequest): Map {
        val map = Map(
            name = request.name,
            minX = 1,
            maxX = request.width,
            minY = 1,
            maxY = request.height
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