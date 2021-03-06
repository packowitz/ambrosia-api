package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class PropertyCategory(
    val levelName: String,
    val value1name: String,
    val showStat: Boolean = false,
    val showResources: Boolean = false,
    val showProgressStat: Boolean = false,
    val showVehicleStat: Boolean = false,
    val showValue2: Boolean = false,
    val value2name: String? = null
) {
    PLAYER("Level", "Value"),
    HERO("Level", "Value"),
    GEAR("Rarity", "From", showStat = true, showValue2 = true, value2name = "To"),
    JEWEL("Jewel Lvl", "Amount", true),
    BATTLE("Threshold", "ArmorReduction", showValue2 = true, value2name = "HealthReduction"),
    SET("NoGear", "Amount", showStat = true),
    BUFF("Intensity", "Bonus", showStat = true),
    RESOURCES("Level", "Amount", showResources = true),
    BUILDING("Building Lvl", "Bonus"),
    UPGRADE_TIME("Building Lvl", "TimeInSec"),
    UPGRADE_COST("Building Lvl", "Amount", showResources = true),
    VEHICLE("Part Lvl", "Bonus", showVehicleStat = true);

    fun getName(): String = name
}
