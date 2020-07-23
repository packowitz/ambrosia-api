package io.pacworx.ambrosia.hero

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.HeroGearSet
import kotlin.math.floor
import kotlin.math.roundToInt

@JsonInclude(JsonInclude.Include.NON_NULL)
data class HeroDto(val id: Long,
                   val missionId: Long?,
                   val playerExpeditionId: Long?,
                   @JsonIgnore
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
                   var markedAsBoss: Boolean,
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
    @JsonIgnore var strengthAbsBonus: Int = 0
    @JsonIgnore var strengthPercBonus: Int = 0
    @JsonIgnore var hpAbsBonus: Int = 0
    @JsonIgnore var hpPercBonus: Int = 0
    @JsonIgnore var armorAbsBonus: Int = 0
    @JsonIgnore var armorPercBonus: Int = 0
    @JsonIgnore var initiativeBonus: Int = 0
    @JsonIgnore var critBonus: Int = 0
    @JsonIgnore var critMultBonus: Int = 0
    @JsonIgnore var dexterityBonus: Int = 0
    @JsonIgnore var resistanceBonus: Int = 0

    // Hidden stats
    @JsonIgnore var lifesteal: Int = 0
    @JsonIgnore var counterChance: Int = 0
    @JsonIgnore var reflect: Int = 0
    @JsonIgnore var dodgeChance: Int = 0
    @JsonIgnore var speedBarFilling: Int = 100
    @JsonIgnore var armorPiercing: Int = 0
    @JsonIgnore var armorExtraDmg: Int = 0
    @JsonIgnore var healthExtraDmg: Int = 0
    @JsonIgnore var redDamageInc: Int = 0
    @JsonIgnore var greenDamageInc: Int = 0
    @JsonIgnore var blueDamageInc: Int = 0
    @JsonIgnore var healingInc: Int = 0
    @JsonIgnore var superCritChance: Int = 0
    @JsonIgnore var buffIntensityInc: Int = 0
    @JsonIgnore var debuffIntensityInc: Int = 0
    @JsonIgnore var buffDurationInc: Int = 0
    @JsonIgnore var debuffDurationInc: Int = 0
    @JsonIgnore var healPerTurn: Int = 0
    @JsonIgnore var dmgPerTurn: Int = 0
    @JsonIgnore var confuseChance: Int = 0
    @JsonIgnore var damageReduction: Int = 0

    constructor(hero: Hero) : this(
        id = hero.id,
        missionId = hero.missionId,
        playerExpeditionId = hero.playerExpeditionId,
        heroBase = hero.heroBase,
        stars = hero.stars,
        level = hero.level,
        xp = hero.xp,
        maxXp = hero.maxXp,
        skill1 = hero.skill1,
        skill2 = hero.skill2,
        skill3 = hero.skill3,
        skill4 = hero.skill4,
        skill5 = hero.skill5,
        skill6 = hero.skill6,
        skill7 = hero.skill7,
        skillPoints = hero.skillPoints,
        ascLvl = hero.ascLvl,
        ascPoints = hero.ascPoints,
        ascPointsMax = hero.ascPointsMax,
        markedAsBoss = hero.markedAsBoss,
        weapon = hero.weapon,
        shield = hero.shield,
        helmet = hero.helmet,
        armor = hero.armor,
        gloves = hero.gloves,
        boots = hero.boots
    )

    @JsonIgnore
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

    fun getHeroBaseId(): Long {
        return this.heroBase.id;
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
