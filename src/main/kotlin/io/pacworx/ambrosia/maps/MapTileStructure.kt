package io.pacworx.ambrosia.maps

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class MapTileStructure(val type: MapTileStructureType) {
    ACADEMY(MapTileStructureType.BUILDING),
    ARENA(MapTileStructureType.BUILDING),
    BAZAAR(MapTileStructureType.BUILDING),
    FORGE(MapTileStructureType.BUILDING),
    GARAGE(MapTileStructureType.BUILDING),
    JEWELRY(MapTileStructureType.BUILDING),
    LABORATORY(MapTileStructureType.BUILDING),

    ENTRANCE(MapTileStructureType.PORTAL),
    EXIT(MapTileStructureType.PORTAL),
    HOUSE_ENTRANCE(MapTileStructureType.PORTAL),
    LADDER(MapTileStructureType.PORTAL),
    PORTAL(MapTileStructureType.PORTAL),
    TUBE(MapTileStructureType.PORTAL),
    WELL(MapTileStructureType.PORTAL),

    DARK_CHEST(MapTileStructureType.CHEST),
    EPIC_CHEST(MapTileStructureType.CHEST),
    GOLDEN_CHEST(MapTileStructureType.CHEST),
    MYTHICAL_CHEST(MapTileStructureType.CHEST),
    SIMPLE_CHEST(MapTileStructureType.CHEST),
    VIOLET_CHEST(MapTileStructureType.CHEST),
    WOODEN_CHEST(MapTileStructureType.CHEST),
    WOODEN_CRATE(MapTileStructureType.CHEST);

    fun getName(): String = name
}