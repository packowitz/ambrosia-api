package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto

enum class HeroStat {
    HP_ABS { override fun apply(hero: HeroDto, bonus: Int) { hero.hpAbsBonus += bonus } },
    HP_PERC { override fun apply(hero: HeroDto, bonus: Int) { hero.hpPercBonus += bonus } },
    ARMOR_ABS { override fun apply(hero: HeroDto, bonus: Int) { hero.armorAbsBonus += bonus } },
    ARMOR_PERC { override fun apply(hero: HeroDto, bonus: Int) { hero.armorPercBonus += bonus } },
    STRENGTH_ABS { override fun apply(hero: HeroDto, bonus: Int) { hero.strengthAbsBonus += bonus } },
    STRENGTH_PERC { override fun apply(hero: HeroDto, bonus: Int) { hero.strengthPercBonus += bonus } },
    CRIT { override fun apply(hero: HeroDto, bonus: Int) { hero.critBonus += bonus } },
    CRIT_MULT { override fun apply(hero: HeroDto, bonus: Int) { hero.critMultBonus += bonus } },
    RESISTANCE { override fun apply(hero: HeroDto, bonus: Int) { hero.resistanceBonus += bonus } },
    DEXTERITY { override fun apply(hero: HeroDto, bonus: Int) { hero.dexterityBonus += bonus } },
    INITIATIVE { override fun apply(hero: HeroDto, bonus: Int) { hero.initiativeBonus += bonus } },
    SPEED { override fun apply(hero: HeroDto, bonus: Int) { hero.speedBarFilling += bonus } },
    LIFESTEAL { override fun apply(hero: HeroDto, bonus: Int) { hero.lifesteal += bonus } },
    COUNTER_CHANCE { override fun apply(hero: HeroDto, bonus: Int) { hero.counterChance += bonus } },
    REFLECT { override fun apply(hero: HeroDto, bonus: Int) { hero.reflect += bonus } },
    EVASION_CHANCE { override fun apply(hero: HeroDto, bonus: Int) { hero.evasionChance += bonus } },
    ARMOR_PIERCING { override fun apply(hero: HeroDto, bonus: Int) { hero.armorPiercing += bonus } },
    ARMOR_EXTRA_DMG { override fun apply(hero: HeroDto, bonus: Int) { hero.armorExtraDmg += bonus } },
    HEALTH_EXTRA_DMG { override fun apply(hero: HeroDto, bonus: Int) { hero.healthExtraDmg += bonus } },
    RED_DMG_INC { override fun apply(hero: HeroDto, bonus: Int) { hero.redDamageInc += bonus } },
    GREEN_DMG_INC { override fun apply(hero: HeroDto, bonus: Int) { hero.greenDamageInc += bonus } },
    BLUE_DMG_INC { override fun apply(hero: HeroDto, bonus: Int) { hero.blueDamageInc += bonus } },
    HEALING_INC { override fun apply(hero: HeroDto, bonus: Int) { hero.healingInc += bonus } },
    SUPER_CRIT_CHANCE { override fun apply(hero: HeroDto, bonus: Int) { hero.superCritChance += bonus } },
    BUFF_INTENSITY_INC { override fun apply(hero: HeroDto, bonus: Int) { hero.buffIntensityInc += bonus } },
    DEBUFF_INTENSITY_INC { override fun apply(hero: HeroDto, bonus: Int) { hero.debuffIntensityInc += bonus } };

    abstract fun apply(hero: HeroDto, bonus: Int)
}
