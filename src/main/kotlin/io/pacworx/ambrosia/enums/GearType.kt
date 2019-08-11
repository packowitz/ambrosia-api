package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

enum class GearType(val stats: List<GearStat>) {
    WEAPON(listOf(GearStat.CRIT, GearStat.CRIT_MULT, GearStat.STRENGTH_ABS, GearStat.STRENGTH_PERC)),
    SHIELD(listOf(GearStat.STRENGTH_ABS, GearStat.STRENGTH_PERC, GearStat.HP_ABS, GearStat.HP_PERC, GearStat.CRIT_MULT)),
    HELMET(listOf(GearStat.STRENGTH_ABS, GearStat.STRENGTH_PERC, GearStat.HP_ABS, GearStat.HP_PERC)),
    ARMOR(listOf(GearStat.HP_ABS, GearStat.HP_PERC, GearStat.ARMOR_ABS, GearStat.ARMOR_PERC)),
    PANTS(listOf(GearStat.SPEED, GearStat.RESISTANCE, GearStat.DEXTERITY)),
    BOOTS(listOf(GearStat.SPEED, GearStat.RESISTANCE, GearStat.DEXTERITY))
}