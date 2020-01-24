package io.pacworx.ambrosia.enums

enum class GearJewelSlot(val gearTypes: List<GearType> = GearType.values().toList()) {
    STRENGTH,
    HP,
    ARMOR,
    BUFFING,
    SPEED(listOf(GearType.PANTS, GearType.BOOTS)),
    CRIT,
    SPECIAL(listOf(GearType.ARMOR))
}
