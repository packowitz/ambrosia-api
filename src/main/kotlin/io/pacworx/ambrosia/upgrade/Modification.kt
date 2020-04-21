package io.pacworx.ambrosia.upgrade

import io.pacworx.ambrosia.properties.PropertyType

enum class Modification(val upTimeProp: PropertyType, val upCostProp: PropertyType) {
    REROLL_QUALITY(PropertyType.GEAR_QUAL_UP_TIME, PropertyType.GEAR_QUAL_UP_COST),
    REROLL_STAT(PropertyType.GEAR_STAT_UP_TIME, PropertyType.GEAR_STAT_UP_COST),
    INC_RARITY(PropertyType.GEAR_INC_UP_TIME, PropertyType.GEAR_INC_UP_COST),
    ADD_JEWEL(PropertyType.GEAR_ADD_JEWEL_UP_TIME, PropertyType.GEAR_ADD_JEWEL_UP_COST),
    REROLL_JEWEL_1(PropertyType.GEAR_JEWEL_UP_TIME, PropertyType.GEAR_JEWEL_UP_COST),
    REROLL_JEWEL_2(PropertyType.GEAR_JEWEL_UP_TIME, PropertyType.GEAR_JEWEL_UP_COST),
    REROLL_JEWEL_3(PropertyType.GEAR_JEWEL_UP_TIME, PropertyType.GEAR_JEWEL_UP_COST),
    REROLL_JEWEL_4(PropertyType.GEAR_JEWEL_UP_TIME, PropertyType.GEAR_JEWEL_UP_COST),
    ADD_SPECIAL_JEWEL(PropertyType.GEAR_ADD_SPECIAL_UP_TIME, PropertyType.GEAR_ADD_SPECIAL_UP_COST)
}
