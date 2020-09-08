package io.pacworx.ambrosia.buildings

enum class BuildingType(val upgradeable: Boolean = true) {
    ACADEMY,
    ARENA(false),
    BARRACKS,
    BAZAAR,
    FORGE,
    GARAGE,
    JEWELRY,
    LABORATORY,
    STORAGE
}