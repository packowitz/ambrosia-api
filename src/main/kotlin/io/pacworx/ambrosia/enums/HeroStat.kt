package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto

enum class HeroStat {
    HP_ABS {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.hpAbsBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    HP_PERC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.hpPercBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    ARMOR_ABS {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorAbsBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    ARMOR_PERC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorPercBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.armorBonus += bonus
        }
    },
    STRENGTH_ABS {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.strengthAbsBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    STRENGTH_PERC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.strengthPercBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    CRIT {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.critBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.critBonus += bonus
        }
    },
    CRIT_MULT {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.critMultBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.critMultBonus += bonus
        }
    },
    RESISTANCE {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.resistanceBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.resistanceBonus += bonus
        }
    },
    DEXTERITY {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.dexterityBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.dexterityBonus += bonus
        }
    },
    INITIATIVE {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.initiativeBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    SPEED {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.speedBarFilling += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.speedBonus += bonus
        }
    },
    LIFESTEAL {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.lifesteal += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.lifestealBonus += bonus
        }
    },
    COUNTER_CHANCE {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.counterChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.counterChanceBonus += bonus
        }
    },
    REFLECT {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.reflect += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.reflectBonus += bonus
        }
    },
    DODGE_CHANCE {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.dodgeChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.dodgeChanceBonus += bonus
        }
    },
    ARMOR_PIERCING {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorPiercing += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.armorPiercingBonus += bonus
        }
    },
    ARMOR_EXTRA_DMG {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorExtraDmg += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.armorExtraDmgBonus += bonus
        }
    },
    HEALTH_EXTRA_DMG {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.healthExtraDmg += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.healthExtraDmgBonus += bonus
        }
    },
    RED_DMG_INC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.redDamageInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.redDamageIncBonus += bonus
        }
    },
    GREEN_DMG_INC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.greenDamageInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.greenDamageIncBonus += bonus
        }
    },
    BLUE_DMG_INC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.blueDamageInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.blueDamageIncBonus += bonus
        }
    },
    HEALING_INC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.healingInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.healingIncBonus += bonus
        }
    },
    SUPER_CRIT_CHANCE {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.superCritChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.superCritChanceBonus += bonus
        }
    },
    BUFF_INTENSITY_INC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.buffIntensityInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.buffIntensityIncBonus += bonus
        }
    },
    DEBUFF_INTENSITY_INC {
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.debuffIntensityInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.debuffIntensityIncBonus += bonus
        }
    };

    abstract fun apply(hero: HeroDto, bonus: Int)
    abstract fun apply(hero: BattleHero, bonus: Int)
}
