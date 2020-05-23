package io.pacworx.ambrosia.hero

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.HeroGearSet
import kotlin.math.floor
import kotlin.math.roundToInt

data class HeroDto(val id: Long,
                   val missionId: Long?,
                   val heroBase: HeroBase,
                   var stars: Int,
                   var level: Int,
                   var xp: Int,
                   var maxXp: Int,
                   var skill1: Int,
                   var skill2: Int?,
                   var skill3: Int?,
                   var skill4: Int?,
                   var skill5: Int?,
                   var skill6: Int?,
                   var skill7: Int?,
                   var skillPoints: Int,
                   var ascLvl: Int,
                   var ascPoints: Int,
                   var ascPointsMax: Int,
                   var weapon: Gear?,
                   var shield: Gear?,
                   var helmet: Gear?,
                   var armor: Gear?,
                   var gloves: Gear?,
                   var boots: Gear?) {

    var baseStrength = if (this.level == 60) {
        heroBase.strengthFull
    } else {
        val bonus = floor((level - 1) * (heroBase.strengthFull - heroBase.strengthBase) / 59.0).toInt()
        heroBase.strengthBase + bonus
    }
    var baseHp = if (this.level == 60) {
        heroBase.hpFull
    } else {
        val bonus = floor((level - 1) * (heroBase.hpFull - heroBase.hpBase) / 59.0).toInt()
        heroBase.hpBase + bonus
    }
    var baseArmor = if (this.level == 60) {
        heroBase.armorFull
    } else {
        val bonus = floor((level - 1) * (heroBase.armorFull - heroBase.armorBase) / 59.0).toInt()
        heroBase.armorBase + bonus
    }
    var baseInitiative = if (ascLvl == heroBase.maxAscLevel) {
        this.heroBase.initiativeAsc
    } else {
        this.heroBase.initiative + floor(ascLvl * (heroBase.initiativeAsc - heroBase.initiative) / heroBase.maxAscLevel.toDouble()).toInt()
    }
    var baseCrit = if (this.ascLvl == heroBase.maxAscLevel) {
        this.heroBase.critAsc
    } else {
        this.heroBase.crit + floor(ascLvl * (heroBase.critAsc - heroBase.crit) / heroBase.maxAscLevel.toDouble()).toInt()
    }
    var baseCritMult = if (this.ascLvl == heroBase.maxAscLevel) {
        this.heroBase.critMultAsc
    } else {
        this.heroBase.critMult + floor(ascLvl * (heroBase.critMultAsc - heroBase.crit) / heroBase.maxAscLevel.toDouble()).toInt()
    }
    var baseDexterity = if (this.ascLvl == heroBase.maxAscLevel) {
        this.heroBase.dexterityAsc
    } else {
        this.heroBase.dexterity + floor(ascLvl * (heroBase.dexterityAsc - heroBase.dexterity) / heroBase.maxAscLevel.toDouble()).toInt()
    }
    var baseResistance = if (this.ascLvl == heroBase.maxAscLevel) {
        this.heroBase.resistanceAsc
    } else {
        this.heroBase.resistance + floor(ascLvl * (heroBase.resistanceAsc - heroBase.resistance) / heroBase.maxAscLevel.toDouble()).toInt()
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var sets = listOf<HeroGearSet>()

    // Stat bonuses
    var strengthAbsBonus: Int = 0
    var strengthPercBonus: Int = 0
    var hpAbsBonus: Int = 0
    var hpPercBonus: Int = 0
    var armorAbsBonus: Int = 0
    var armorPercBonus: Int = 0
    var initiativeBonus: Int = 0
    var critBonus: Int = 0
    var critMultBonus: Int = 0
    var dexterityBonus: Int = 0
    var resistanceBonus: Int = 0

    // Hidden stats
    var lifesteal: Int = 0
    var counterChance: Int = 0
    var reflect: Int = 0
    var dodgeChance: Int = 0
    var speedBarFilling: Int = 100
    var armorPiercing: Int = 0
    var armorExtraDmg: Int = 0
    var healthExtraDmg: Int = 0
    var redDamageInc: Int = 0
    var greenDamageInc: Int = 0
    var blueDamageInc: Int = 0
    var healingInc: Int = 0
    var superCritChance: Int = 0
    var buffIntensityInc: Int = 0
    var debuffIntensityInc: Int = 0
    var buffDurationInc: Int = 0
    var debuffDurationInc: Int = 0
    var healPerTurn: Int = 0
    var dmgPerTurn: Int = 0
    var confuseChance: Int = 0
    var damageReduction: Int = 0

    constructor(hero: Hero) : this(hero.id, hero.missionId, hero.heroBase, hero.stars, hero.level, hero.xp, hero.maxXp, hero.skill1, hero.skill2, hero.skill3, hero.skill4, hero.skill5, hero.skill6, hero.skill7, hero.skillPoints, hero.ascLvl, hero.ascPoints, hero.ascPointsMax, hero.weapon, hero.shield, hero.helmet, hero.armor, hero.gloves, hero.boots)

    fun getGears(): List<Gear> {
        val gears = mutableListOf<Gear>()
        weapon?.let { gears.add(it) }
        shield?.let { gears.add(it) }
        helmet?.let { gears.add(it) }
        armor?.let { gears.add(it) }
        gloves?.let { gears.add(it) }
        boots?.let { gears.add(it) }
        return gears
    }

    fun getStrengthTotal(): Int =
            baseStrength + strengthAbsBonus + (baseStrength * (strengthPercBonus / 100.0)).roundToInt()

    fun getHpTotal(): Int =
            baseHp + hpAbsBonus + (baseHp * (hpPercBonus / 100.0)).roundToInt()

    fun getArmorTotal(): Int =
            baseArmor + armorAbsBonus + (baseArmor * (armorPercBonus / 100.0)).roundToInt()

    fun getInitiativeTotal(): Int = baseInitiative + initiativeBonus

    fun getCritTotal(): Int = baseCrit + critBonus

    fun getCritMultTotal(): Int = baseCritMult + critMultBonus

    fun getDexterityTotal(): Int = baseDexterity + dexterityBonus

    fun getResistanceTotal(): Int = baseResistance + resistanceBonus

    fun getSkillLevel(skillNr: Int): Int {
        return when(skillNr) {
            1 -> skill1
            2 -> skill2 ?: 0
            3 -> skill3 ?: 0
            4 -> skill4 ?: 0
            5 -> skill5 ?: 0
            6 -> skill6 ?: 0
            7 -> skill7 ?: 0
            else -> 0
        }
    }
}
