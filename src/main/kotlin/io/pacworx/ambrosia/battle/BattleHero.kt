package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import javax.persistence.*

@Entity
data class BattleHero(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Enumerated(EnumType.STRING)
        var status: HeroStatus = HeroStatus.ALIVE,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "battle_hero_id")
        var buffs: MutableList<BattleHeroBuff> = mutableListOf(),

        val heroStrength: Int,
        var strengthBonus: Int = 0,
        val heroHp: Int,
        val heroArmor: Int,
        val heroInitiative: Int,
        val heroCrit: Int,
        var critBonus: Int = 0,
        val heroCritMult: Int,
        var critMultBonus: Int = 0,
        val heroDexterity: Int,
        var dexterityBonus: Int = 0,
        val heroResistance: Int,
        var resistanceBonus: Int = 0,

        var currentHp: Int = heroHp,
        var currentArmor: Int = heroArmor,
        var currentSpeedBar: Int = heroInitiative,

        val heroLifesteal: Int,
        var lifestealBonus: Int = 0,
        val heroCounterChance: Int,
        var counterChanceBonus: Int = 0,
        val heroReflect: Int,
        var reflectBonus: Int = 0,
        val heroDodgeChance: Int,
        var dodgeChanceBonus: Int = 0,
        val heroSpeed: Int,
        var speedBonus: Int = 0,
        val heroArmorPiercing: Int,
        var armorPiercingBonus: Int = 0,
        val heroArmorExtraDmg: Int,
        var armorExtraDmgBonus: Int = 0,
        val heroHealthExtraDmg: Int,
        var healthExtraDmgBonus: Int = 0,
        val heroRedDamageInc: Int,
        var redDamageIncBonus: Int = 0,
        val heroGreenDamageInc: Int,
        var greenDamageIncBonus: Int = 0,
        val heroBlueDamageInc: Int,
        var blueDamageIncBonus: Int = 0,
        val heroHealingInc: Int,
        var healingIncBonus: Int = 0,
        val heroSuperCritChance: Int,
        var superCritChanceBonus: Int = 0,
        val heroBuffIntensityInc: Int,
        var buffIntensityIncBonus: Int = 0,
        val heroDebuffIntensityInc: Int,
        var debuffIntensityIncBonus: Int = 0
) {
    constructor(hero: HeroDto): this() {

    }

    fun resetBonus() {
        if (currentHp > 0) {
            status = HeroStatus.ALIVE
        } else {
            status = HeroStatus.DEAD
        }
        strengthBonus = 0
        critBonus = 0
        dexterityBonus = 0
        resistanceBonus = 0
        lifestealBonus = 0
        counterChanceBonus = 0
        reflectBonus = 0
        dodgeChanceBonus = 0
        speedBonus = 0
        armorPiercingBonus = 0
        armorExtraDmgBonus = 0
        healthExtraDmgBonus = 0
        redDamageIncBonus = 0
        greenDamageIncBonus = 0
        blueDamageIncBonus = 0
        healingIncBonus = 0
        superCritChanceBonus = 0
        buffIntensityIncBonus = 0
        debuffIntensityIncBonus = 0
    }
}
