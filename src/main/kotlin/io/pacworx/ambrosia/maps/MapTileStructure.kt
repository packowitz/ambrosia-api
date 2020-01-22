package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class MapTileStructure(val type: MapTileStructureType) {
    ACADEMY(MapTileStructureType.BUILDING),
    //ARENA(MapTileStructureType.BUILDING),
    BAZAAR(MapTileStructureType.BUILDING),
    BLACKSMITH(MapTileStructureType.BUILDING),
    GARAGE(MapTileStructureType.BUILDING),
    JEWELRY(MapTileStructureType.BUILDING),
    LABORATORY(MapTileStructureType.BUILDING),

    ENTRANCE(MapTileStructureType.PORTAL),
    EXIT(MapTileStructureType.PORTAL),
    PORTAL(MapTileStructureType.PORTAL);

    fun getName(): String = name
}