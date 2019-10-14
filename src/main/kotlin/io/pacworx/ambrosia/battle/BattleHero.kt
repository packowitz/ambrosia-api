package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Buff
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Color
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
        var priority: Int = 0,

        @ManyToOne
        @JoinColumn(name = "hero_base_id")
        val heroBase: HeroBase,

        @Enumerated(EnumType.STRING)
        val position: HeroPosition,
        @Enumerated(EnumType.STRING)
        val color: Color,
        val level: Int,
        val stars: Int,
        val ascLvl: Int,

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
        @field:Transient var strengthBonus: Int = 0,
        val heroHp: Int,
        val heroArmor: Int,
        @field:Transient var armorBonus: Int = 0,
        val heroCrit: Int,
        @field:Transient var critBonus: Int = 0,
        val heroCritMult: Int,
        @field:Transient var critMultBonus: Int = 0,
        val heroDexterity: Int,
        @field:Transient var dexterityBonus: Int = 0,
        val heroResistance: Int,
        @field:Transient var resistanceBonus: Int = 0,

        var currentHp: Int = heroHp,
        var currentArmor: Int = heroArmor,
        var currentSpeedBar: Int,

        val heroLifesteal: Int,
        @field:Transient var lifestealBonus: Int = 0,
        val heroCounterChance: Int,
        @field:Transient var counterChanceBonus: Int = 0,
        val heroReflect: Int,
        @field:Transient var reflectBonus: Int = 0,
        val heroDodgeChance: Int,
        @field:Transient var dodgeChanceBonus: Int = 0,
        val heroSpeed: Int,
        @field:Transient var speedBonus: Int = 0,
        val heroArmorPiercing: Int,
        @field:Transient var armorPiercingBonus: Int = 0,
        val heroArmorExtraDmg: Int,
        @field:Transient var armorExtraDmgBonus: Int = 0,
        val heroHealthExtraDmg: Int,
        @field:Transient var healthExtraDmgBonus: Int = 0,
        val heroRedDamageInc: Int,
        @field:Transient var redDamageIncBonus: Int = 0,
        val heroGreenDamageInc: Int,
        @field:Transient var greenDamageIncBonus: Int = 0,
        val heroBlueDamageInc: Int,
        @field:Transient var blueDamageIncBonus: Int = 0,
        val heroHealingInc: Int,
        @field:Transient var healingIncBonus: Int = 0,
        val heroSuperCritChance: Int,
        @field:Transient var superCritChanceBonus: Int = 0,
        val heroBuffIntensityInc: Int,
        @field:Transient var buffIntensityIncBonus: Int = 0,
        val heroDebuffIntensityInc: Int,
        @field:Transient var debuffIntensityIncBonus: Int = 0,
        val heroBuffDurationInc: Int,
        @field:Transient var buffDurationIncBonus: Int = 0,
        val heroDebuffDurationInc: Int,
        @field:Transient var debuffDurationIncBonus: Int = 0,
        val heroHealPerTurn: Int,
        @field:Transient var healPerTurnBonus: Int = 0,
        val heroDmgPerTurn: Int,
        @field:Transient var dmgPerTurnBonus: Int = 0
) {
    constructor(playerId: Long, hero: HeroDto, heroBase: HeroBase, position: HeroPosition) : this(
            heroBase = heroBase,
            playerId = playerId,
            position = position,
            color = heroBase.color,
            level = hero.level,
            stars = hero.stars,
            ascLvl = hero.ascLvl,
            skill1Lvl = hero.skill1,
            skill2Lvl = hero.skill2,
            skill2Cooldown = hero.skill2?.let { heroBase.skills.find { it.number == 2 }?.initCooldown },
            skill3Lvl = hero.skill3,
            skill3Cooldown = hero.skill3?.let { heroBase.skills.find { it.number == 3 }?.initCooldown },
            skill4Lvl = hero.skill4,
            skill4Cooldown = hero.skill4?.let { heroBase.skills.find { it.number == 4 }?.initCooldown },
            skill5Lvl = hero.skill5,
            skill5Cooldown = hero.skill5?.let { heroBase.skills.find { it.number == 5 }?.initCooldown },
            skill6Lvl = hero.skill6,
            skill6Cooldown = hero.skill6?.let { heroBase.skills.find { it.number == 6 }?.initCooldown },
            skill7Lvl = hero.skill7,
            skill7Cooldown = hero.skill7?.let { heroBase.skills.find { it.number == 7 }?.initCooldown },
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
            heroDebuffIntensityInc = hero.debuffIntensityInc,
            heroBuffDurationInc = hero.buffDurationInc,
            heroDebuffDurationInc = hero.debuffDurationInc,
            heroHealPerTurn = hero.healPerTurn,
            heroDmgPerTurn = hero.dmgPerTurn
    )

    fun resetBonus(battle: Battle, propertyService: PropertyService) {
        strengthBonus = 0
        armorBonus = 0
        critBonus = 0
        critMultBonus = 0
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
        buffDurationIncBonus = 0
        debuffDurationIncBonus = 0
        healPerTurnBonus = 0
        dmgPerTurnBonus = 0

        buffs.forEach { it.buff.applyEffect(battle, this, it, propertyService) }
    }

    fun initTurn(battle: Battle, propertyService: PropertyService) {
        skill2Cooldown = skill2Cooldown?.let { it.takeIf { it > 0 }?.dec() ?: 0 }
        skill3Cooldown = skill3Cooldown?.let { it.takeIf { it > 0 }?.dec() ?: 0 }
        skill4Cooldown = skill4Cooldown?.let { it.takeIf { it > 0 }?.dec() ?: 0 }
        skill5Cooldown = skill5Cooldown?.let { it.takeIf { it > 0 }?.dec() ?: 0 }
        skill6Cooldown = skill6Cooldown?.let { it.takeIf { it > 0 }?.dec() ?: 0 }
        skill7Cooldown = skill7Cooldown?.let { it.takeIf { it > 0 }?.dec() ?: 0 }

        //Buffs
        buffs.forEach {
            it.buff.preTurnAction(battle, this, it, propertyService)
            it.decreaseDuration()
        }
        if (heroDmgPerTurn + dmgPerTurnBonus > 0) {
            val damage = heroHp * (heroDmgPerTurn + dmgPerTurnBonus) / 100
            currentHp -= damage
            battle.getPreTurnStep().addAction(BattleStepAction(
                    heroPosition = this.position,
                    type = BattleStepActionType.DOT,
                    healthDiff = -damage
            ))
        }
        if (heroHealPerTurn + healPerTurnBonus > 0) {
            var healing = heroHp * (heroHealPerTurn + healPerTurnBonus) / 100
            if (heroHealingInc + healingIncBonus > 0) {
                healing *= (heroHealingInc + healingIncBonus) / 100
            }
            currentHp += healing
            battle.getPreTurnStep().addAction(BattleStepAction(
                    heroPosition = this.position,
                    type = BattleStepActionType.HOT,
                    healthDiff = healing
            ))
        }
        if (currentHp >= heroHp) {
            currentHp = heroHp
        }
        if (currentHp <= 0) {
            status = HeroStatus.DEAD
            battle.getPreTurnStep().addAction(BattleStepAction(
                    heroPosition = this.position,
                    type = BattleStepActionType.DEAD
            ))
        }
    }

    fun afterTurn(battle: Battle, propertyService: PropertyService) {
        buffs.removeIf { it.duration == 0 }
        battle.allHeroesAlive().forEach { it.resetBonus(battle, propertyService) }
    }

    fun getCooldown(skillNr: Int): Int {
        return when (skillNr) {
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

    fun skillUsed(skillNr: Int) {
        when (skillNr) {
            2 -> skill2Cooldown = heroBase.skills.find { it.number == 2 }?.cooldown
            3 -> skill3Cooldown = heroBase.skills.find { it.number == 3 }?.cooldown
            4 -> skill4Cooldown = heroBase.skills.find { it.number == 4 }?.cooldown
            5 -> skill5Cooldown = heroBase.skills.find { it.number == 5 }?.cooldown
            6 -> skill6Cooldown = heroBase.skills.find { it.number == 6 }?.cooldown
            7 -> skill7Cooldown = heroBase.skills.find { it.number == 7 }?.cooldown
        }
    }

    fun getTotalStrength(): Int = heroStrength + ((heroStrength * strengthBonus) / 100)

    fun getTotalArmor(): Int = currentArmor + ((heroArmor * armorBonus) / 100)

    fun getTotalMaxArmor(): Int = heroArmor + ((heroArmor * armorBonus) / 100)

    fun getTotalCrit(): Int = heroCrit + critBonus

    fun getTotalCritMult(): Int = heroCritMult + critMultBonus

    fun getTotalResistance(): Int = heroResistance + resistanceBonus

    fun getTotalDexterity(): Int = heroDexterity + dexterityBonus

    fun isTaunting(): Boolean = buffs.any { it.buff == Buff.TAUNT_BUFF }
}
