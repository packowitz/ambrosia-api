package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import io.pacworx.ambrosia.models.HeroBase
import javax.persistence.*

@Entity
data class BattleHero(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING)
    var status: HeroStatus = HeroStatus.ALIVE,

    @ManyToOne
    @JoinColumn(name = "hero_base_id")
    val heroBase: HeroBase,

    @Enumerated(EnumType.STRING)
    val position: HeroPosition,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "battle_hero_id")
    var buffs: MutableList<BattleHeroBuff> = mutableListOf(),

    val skill1Lvl: Int,
    val skill2Lvl: Int?,
    var skill2Cooldown: Int?,
    val skill3Lvl: Int?,
    var skill3Cooldown: Int?,
    val skill4Lvl: Int?,
    var skill4Cooldown: Int?,
    val skill5Lvl: Int?,
    var skill5Cooldown: Int?,
    val skill6Lvl: Int?,
    var skill6Cooldown: Int?,
    val skill7Lvl: Int?,
    var skill7Cooldown: Int?,

    val heroStrength: Int,
    var strengthBonus: Int = 0,
    val heroHp: Int,
    val heroArmor: Int,
    var armorBonus: Int = 0,
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
    var currentSpeedBar: Int,

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
    constructor(playerId: Long, hero: HeroDto, heroBase: HeroBase, position: HeroPosition) : this(
        heroBase = heroBase,
        playerId = playerId,
        position = position,
        skill1Lvl = hero.skill1,
        skill2Lvl = hero.skill2,
        skill2Cooldown = hero.skill2?.let { heroBase.skills.find { it.number == 2 }?.let { it.initCooldown } },
        skill3Lvl = hero.skill3,
        skill3Cooldown = hero.skill3?.let { heroBase.skills.find { it.number == 3 }?.let { it.initCooldown } },
        skill4Lvl = hero.skill4,
        skill4Cooldown = hero.skill4?.let { heroBase.skills.find { it.number == 4 }?.let { it.initCooldown } },
        skill5Lvl = hero.skill5,
        skill5Cooldown = hero.skill5?.let { heroBase.skills.find { it.number == 5 }?.let { it.initCooldown } },
        skill6Lvl = hero.skill6,
        skill6Cooldown = hero.skill6?.let { heroBase.skills.find { it.number == 6 }?.let { it.initCooldown } },
        skill7Lvl = hero.skill7,
        skill7Cooldown = hero.skill7?.let { heroBase.skills.find { it.number == 7 }?.let { it.initCooldown } },
        heroStrength = hero.getStrengthTotal(),
        heroHp = hero.getHpTotal(),
        heroArmor = hero.getArmorTotal(),
        currentSpeedBar = hero.getInitiativeTotal(),
        heroCrit = hero.getCritTotal(),
        heroCritMult = hero.getCritMultTotal(),
        heroDexterity = hero.getDexterityTotal(),
        heroResistance = hero.getResistanceTotal(),
        heroLifesteal = hero.lifesteal,
        heroCounterChance = hero.counterChance,
        heroReflect = hero.reflect,
        heroDodgeChance = hero.dodgeChance,
        heroSpeed = hero.speedBarFilling,
        heroArmorPiercing = hero.armorPiercing,
        heroArmorExtraDmg = hero.armorExtraDmg,
        heroHealthExtraDmg = hero.healthExtraDmg,
        heroRedDamageInc = hero.redDamageInc,
        heroGreenDamageInc = hero.greenDamageInc,
        heroBlueDamageInc = hero.blueDamageInc,
        heroHealingInc = hero.healingInc,
        heroSuperCritChance = hero.superCritChance,
        heroBuffIntensityInc = hero.buffIntensityInc,
        heroDebuffIntensityInc = hero.debuffIntensityInc
    )

    fun resetBonus(battle: Battle, propertyService: PropertyService) {
        if (currentHp > 0) {
            status = HeroStatus.ALIVE
        } else {
            status = HeroStatus.DEAD
        }
        strengthBonus = 0
        armorBonus = 0
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

        buffs.forEach { it.buff.applyEffect(battle, this, it, propertyService) }
    }

    fun initTurn(battle: Battle, propertyService: PropertyService) {
        skill2Cooldown?.takeIf { it > 0 }?.dec()
        skill3Cooldown?.takeIf { it > 0 }?.dec()
        skill4Cooldown?.takeIf { it > 0 }?.dec()
        skill5Cooldown?.takeIf { it > 0 }?.dec()
        skill6Cooldown?.takeIf { it > 0 }?.dec()
        skill7Cooldown?.takeIf { it > 0 }?.dec()

        //Buffs
        buffs.forEach {
            it.buff.preTurnAction(battle, this, it, propertyService)
        }
        if (currentHp >= heroHp) {
            currentHp = heroHp
        }
        if (currentHp <=0) {
            status = HeroStatus.DEAD
        }
    }

    fun afterTurn(battle: Battle, propertyService: PropertyService) {
        buffs.forEach { it.decreaseDuration() }
        buffs.removeIf { it.duration == 0 }
        resetBonus(battle, propertyService)
    }

    fun getCooldown(skillNr: Int): Int {
        return when(skillNr) {
            1 -> 0
            2 -> skill2Cooldown
            3 -> skill3Cooldown
            4 -> skill4Cooldown
            5 -> skill5Cooldown
            6 -> skill6Cooldown
            7 -> skill7Cooldown
            else -> null
        } ?: 99
    }
}
