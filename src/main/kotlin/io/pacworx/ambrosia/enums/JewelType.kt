package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class JewelType(val slot: GearJewelSlot, val gearSet: GearSet? = null) {
    HP_ABS(GearJewelSlot.HP),
    HP_PERC(GearJewelSlot.HP),
    ARMOR_ABS(GearJewelSlot.ARMOR),
    ARMOR_PERC(GearJewelSlot.ARMOR),
    STRENGTH_ABS(GearJewelSlot.STRENGTH),
    STRENGTH_PERC(GearJewelSlot.STRENGTH),
    CRIT(GearJewelSlot.CRIT),
    CRIT_MULT(GearJewelSlot.CRIT),
    RESISTANCE(GearJewelSlot.BUFFING),
    DEXTERITY(GearJewelSlot.BUFFING),
    INITIATIVE(GearJewelSlot.SPEED),
    SPEED(GearJewelSlot.SPEED),
    STONE_SKIN(GearJewelSlot.SPECIAL, GearSet.STONE_SKIN),
    VITAL_AURA(GearJewelSlot.SPECIAL, GearSet.VITAL_AURA),
    POWER_FIST(GearJewelSlot.SPECIAL, GearSet.POWER_FIST),
    BUFFS_BLESSING(GearJewelSlot.SPECIAL, GearSet.BUFFS_BLESSING),
    BERSERKERS_AXE(GearJewelSlot.SPECIAL, GearSet.BERSERKERS_AXE),
    MYTHICAL_MIRROR(GearJewelSlot.SPECIAL, GearSet.MYTHICAL_MIRROR),
    WARHORN(GearJewelSlot.SPECIAL, GearSet.WARHORN),
    REVERSED_REALITY(GearJewelSlot.SPECIAL, GearSet.REVERSED_REALITY);

    fun getName(): String = name
}
