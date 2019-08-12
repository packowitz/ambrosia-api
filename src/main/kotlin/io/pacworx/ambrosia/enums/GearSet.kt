package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero

enum class GearSet(val number: Int, val description: String) {
    STONE_SKIN(2, "Armor increased by 25%") {
        override fun apply(hero: Hero) {
            hero.armorPercBonus += 25
        }
    },
    VITAL_AURA(2, "Hitpoints increased by 25%") {
        override fun apply(hero: Hero) {
            hero.hpPercBonus += 25
        }
    },
    POWER_FIST(2, "Strength increased by 25%") {
        override fun apply(hero: Hero) {
            hero.strengthPercBonus += 25
        }
    };

    abstract fun apply(hero: Hero)
}
