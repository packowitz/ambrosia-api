package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.Map
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapRepository
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("map")
class MapController(private val mapRepository: MapRepository) {

    @GetMapping
    fun getAllMaps(): List<Map> = mapRepository.findAll()
}
