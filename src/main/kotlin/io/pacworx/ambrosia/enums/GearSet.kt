package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class GearSet(val pieces: Int, val description: String) {
    STONE_SKIN(2, "Armor increased by 25%"),
    VITAL_AURA(2, "Hitpoints increased by 25%"),
    POWER_FIST(2, "Strength increased by 25%");

    fun getName(): String = name
}
