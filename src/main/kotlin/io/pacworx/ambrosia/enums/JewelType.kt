package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero

enum class JewelType(val slot: GearJewelSlot, val gearSet: GearSet? = null) {
    HP_ABS(GearJewelSlot.HP) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.hpAbsBonus += 10 * lvl
        }
    },
    HP_PERC(GearJewelSlot.HP) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.hpPercBonus += 10 * lvl
        }
    },
    ARMOR_ABS(GearJewelSlot.ARMOR) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.armorAbsBonus += 10 * lvl
        }
    },
    ARMOR_PERC(GearJewelSlot.ARMOR) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.armorPercBonus += 10 * lvl
        }
    },
    STRENGTH_ABS(GearJewelSlot.STRENGTH) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.strengthAbsBonus += 10 * lvl
        }
    },
    STRENGTH_PERC(GearJewelSlot.STRENGTH) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.strengthPercBonus += 10 * lvl
        }
    },
    CRIT(GearJewelSlot.CRIT) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.critBonus += lvl
        }
    },
    CRIT_MULT(GearJewelSlot.CRIT) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.critMultBonus += lvl
        }
    },
    RESISTANCE(GearJewelSlot.BUFFING) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.resistanceBonus += lvl
        }
    },
    DEXTERITY(GearJewelSlot.BUFFING) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.dexterityBonus += lvl
        }
    },
    INITIATIVE(GearJewelSlot.SPEED) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.initiativeBonus += 10 * lvl
        }
    },
    SPEED(GearJewelSlot.SPEED) {
        override fun apply(hero: Hero, lvl: Int) {
            hero.speedBarFilling += 0.01 * lvl
        }
    },
    STONE_SKIN(GearJewelSlot.SPECIAL, GearSet.STONE_SKIN) {
        override fun apply(hero: Hero, lvl: Int) {}
    },
    VITAL_AURA(GearJewelSlot.SPECIAL, GearSet.VITAL_AURA) {
        override fun apply(hero: Hero, lvl: Int) {}
    },
    POWER_FIST(GearJewelSlot.SPECIAL, GearSet.POWER_FIST) {
        override fun apply(hero: Hero, lvl: Int) {}
    };

    abstract fun apply(hero: Hero, lvl: Int)
}
