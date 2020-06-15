package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.battle.BattleHero
import io.pacworx.ambrosia.battle.BattleService.Companion.SPEEDBAR_MAX

enum class HeroStat {
    HP_ABS {
        override fun desc(bonus: Int): String {
            return "+$bonus hitpoints"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.hpAbsBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    HP_PERC {
        override fun desc(bonus: Int): String {
            return "+$bonus% hitpoints"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.hpPercBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    ARMOR_ABS {
        override fun desc(bonus: Int): String {
            return "+$bonus armor"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorAbsBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    ARMOR_PERC {
        override fun desc(bonus: Int): String {
            return "+$bonus% armor"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorPercBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.armorBonus += bonus
        }
    },
    STRENGTH_ABS {
        override fun desc(bonus: Int): String {
            return "+$bonus strength"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.strengthAbsBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    STRENGTH_PERC {
        override fun desc(bonus: Int): String {
            return "+$bonus% strength"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.strengthPercBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    CRIT {
        override fun desc(bonus: Int): String {
            return "+$bonus% crit chance"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.critBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.critBonus += bonus
        }
    },
    CRIT_MULT {
        override fun desc(bonus: Int): String {
            return "+$bonus% crit damage"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.critMultBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.critMultBonus += bonus
        }
    },
    RESISTANCE {
        override fun desc(bonus: Int): String {
            return "+$bonus resistance"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.resistanceBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.resistanceBonus += bonus
        }
    },
    DEXTERITY {
        override fun desc(bonus: Int): String {
            return "+$bonus dexterity"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.dexterityBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.dexterityBonus += bonus
        }
    },
    INITIATIVE {
        override fun desc(bonus: Int): String {
            return "+$bonus initiative"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.initiativeBonus += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {}
    },
    SPEED {
        override fun desc(bonus: Int): String {
            return "+$bonus speed"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.speedBarFilling += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.speedBonus += bonus
        }
    },
    LIFESTEAL {
        override fun desc(bonus: Int): String {
            return "+$bonus% lifesteal"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.lifesteal += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.lifestealBonus += bonus
        }
    },
    COUNTER_CHANCE {
        override fun desc(bonus: Int): String {
            return "+$bonus% counter chance"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.counterChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.counterChanceBonus += bonus
        }
    },
    REFLECT {
        override fun desc(bonus: Int): String {
            return "+$bonus% reflect damage"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.reflect += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.reflectBonus += bonus
        }
    },
    DODGE_CHANCE {
        override fun desc(bonus: Int): String {
            return "+$bonus% dodge chance"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.dodgeChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.dodgeChanceBonus += bonus
        }
    },
    ARMOR_PIERCING {
        override fun desc(bonus: Int): String {
            return "+$bonus% armor piercing"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorPiercing += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.armorPiercingBonus += bonus
        }
    },
    ARMOR_EXTRA_DMG {
        override fun desc(bonus: Int): String {
            return "+$bonus% extra damage against armor"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.armorExtraDmg += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.armorExtraDmgBonus += bonus
        }
    },
    HEALTH_EXTRA_DMG {
        override fun desc(bonus: Int): String {
            return "+$bonus% extra damage after armor"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.healthExtraDmg += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.healthExtraDmgBonus += bonus
        }
    },
    RED_DMG_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus% damage against red heroes"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.redDamageInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.redDamageIncBonus += bonus
        }
    },
    GREEN_DMG_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus% damage against green heroes"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.greenDamageInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.greenDamageIncBonus += bonus
        }
    },
    BLUE_DMG_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus% damage against blue heroes"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.blueDamageInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.blueDamageIncBonus += bonus
        }
    },
    HEALING_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus% increased received healing"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.healingInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.healingIncBonus += bonus
        }
    },
    SUPER_CRIT_CHANCE {
        override fun desc(bonus: Int): String {
            return "+$bonus% chance to super crit"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.superCritChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.superCritChanceBonus += bonus
        }
    },
    BUFF_INTENSITY_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus buff intesity"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.buffIntensityInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.buffIntensityIncBonus += bonus
        }
    },
    DEBUFF_INTENSITY_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus debuff intesity"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.debuffIntensityInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.debuffIntensityIncBonus += bonus
        }
    },
    BUFF_DURATION_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus buff duration"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.buffDurationInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.buffDurationIncBonus += bonus
        }
    },
    DEBUFF_DURATION_INC {
        override fun desc(bonus: Int): String {
            return "+$bonus debuff duration"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.debuffDurationInc += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.debuffDurationIncBonus += bonus
        }
    },
    HEAL_PER_TURN {
        override fun desc(bonus: Int): String {
            return "+$bonus% healing per turn"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.healPerTurn += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.healPerTurnBonus += bonus
        }
    },
    DMG_PER_TURN {
        override fun desc(bonus: Int): String {
            return "+$bonus% damage per turn"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.dmgPerTurn += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.dmgPerTurnBonus += bonus
        }
    },
    CONFUSE_CHANCE {
        override fun desc(bonus: Int): String {
            return "+$bonus% chance of being confused"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.confuseChance += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.confuseChanceBonus += bonus
        }
    },
    DAMAGE_REDUCTION {
        override fun desc(bonus: Int): String {
            return "+$bonus% damage reduction"
        }
        override fun apply(hero: HeroDto, bonus: Int) {
            hero.damageReduction += bonus
        }
        override fun apply(hero: BattleHero, bonus: Int) {
            hero.damageReductionBonus += bonus
        }
    },
    BUFF_RESISTANCE,
    INIT_SPEEDBAR_GAIN {
        override fun initTurn(hero: BattleHero, bonus: Int) {
            hero.currentSpeedBar += (bonus * SPEEDBAR_MAX) / 100
        }
    };

    open fun desc(bonus: Int): String = ""
    open fun apply(hero: HeroDto, bonus: Int) {}
    open fun apply(hero: BattleHero, bonus: Int) {}
    open fun initTurn(hero: BattleHero, bonus: Int) {}
}
