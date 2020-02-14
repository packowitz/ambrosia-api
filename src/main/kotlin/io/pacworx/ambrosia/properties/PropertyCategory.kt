package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class PropertyCategory(
    val levelName: String,
    val value1name: String,
    val showStat: Boolean = false,
    val showResources: Boolean = false,
    val showBuilding: Boolean = false,
    val showVehicleStat: Boolean = false,
    val showValue2: Boolean = false,
    val value2name: String? = null
) {
    HERO("Level", "Value"),
    GEAR("Rarity", "From", true, false, false, false, true, "To"),
    JEWEL("Jewel Lvl", "Amount", true),
    BATTLE("Threshold", "ArmorReduction", false, false, false, false, true, "HealthReduction"),
    SET("NoGear", "Amount", true),
    BUFF("Intensity", "Bonus", true),
    RESOURCES("Level", "Amount", false, true),
    BUILDING("Building Lvl", "Value", false, false, true),
    VEHICLE("Part Lvl", "Bonus", false, false, false, true);

    fun getName(): String = name
}
