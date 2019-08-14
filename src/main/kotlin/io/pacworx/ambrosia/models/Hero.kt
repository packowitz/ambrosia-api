package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.GearSet
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.GearType
import io.pacworx.ambrosia.models.HeroBase
import javax.annotation.PostConstruct
import javax.persistence.*
import kotlin.math.roundToInt

@Entity
data class Hero(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @ManyToOne
    @JoinColumn(name = "hero_base_id")
    val heroBase: HeroBase,
    var stars: Int,
    var level: Int = 1,
    var xp: Int = 0,
    var maxXp: Int,
    var skill1: Int = 0,
    var skill2: Int = 0,
    var skill3: Int = 0,
    var skill4: Int = 0,
    var skill5: Int = 0,
    var skill6: Int = 0,
    var skill7: Int = 0,
    var ascLvl: Int = 0,
    var ascPoints: Int = 0,
    var ascPointsMax: Int,
    @OneToOne
    @JoinColumn(name = "weapon_id")
    var weapon: Gear? = null,
    @OneToOne
    @JoinColumn(name = "shield_id")
    var shield: Gear? = null,
    @OneToOne
    @JoinColumn(name = "helmet_id")
    var helmet: Gear? = null,
    @OneToOne
    @JoinColumn(name = "armor_id")
    var armor: Gear? = null,
    @OneToOne
    @JoinColumn(name = "pants_id")
    var pants: Gear? = null,
    @OneToOne
    @JoinColumn(name = "boots_id")
    var boots: Gear? = null
) {

    @Transient var baseStrength: Int = 0
    @Transient var baseHp: Int = 0
    @Transient var baseArmor: Int = 0
    @Transient var baseInitiative: Int = 0
    @Transient var baseCrit: Int = 0
    @Transient var baseCritMult: Int = 0
    @Transient var baseDexterity: Int = 0
    @Transient var baseResistance: Int = 0

    @Transient var sets = mutableListOf<GearSet>()

    // Stat bonuses
    @Transient var strengthAbsBonus: Int = 0
    @Transient var strengthPercBonus: Int = 0
    @Transient var hpAbsBonus: Int = 0
    @Transient var hpPercBonus: Int = 0
    @Transient var armorAbsBonus: Int = 0
    @Transient var armorPercBonus: Int = 0
    @Transient var initiativeBonus: Int = 0
    @Transient var critBonus: Int = 0
    @Transient var critMultBonus: Int = 0
    @Transient var dexterityBonus: Int = 0
    @Transient var resistanceBonus: Int = 0

    // Hidden stats
    @Transient var lifesteal: Int = 0
    @Transient var counterChance: Int = 0
    @Transient var reflect: Int = 0
    @Transient var evasionChance: Int = 0
    @Transient var speedBarFilling: Int = 100
    @Transient var armorPiercing: Int = 0
    @Transient var armorExtraDmg: Int = 0
    @Transient var healthExtraDmg: Int = 0
    @Transient var redDamageInc: Int = 0
    @Transient var greenDamageInc: Int = 0
    @Transient var blueDamageInc: Int = 0
    @Transient var healingInc: Int = 0
    @Transient var superCritChance: Int = 0
    @Transient var buffIntensityInc: Int = 0
    @Transient var debuffIntensityInc: Int = 0

    constructor(playerId: Long, heroBase: HeroBase) : this(
        playerId = playerId,
        heroBase = heroBase,
        stars = heroBase.rarity.stars,
        maxXp = 10000,
        ascPointsMax = 10000
    )

    private fun initVars() {
        baseStrength = if (this.level == 60) {
            heroBase.strengthFull
        } else {
            val bonus = Math.floor((level - 1) * (heroBase.strengthFull - heroBase.strengthBase) / 59.0).toInt()
            heroBase.strengthBase + bonus
        }
        baseHp = if (this.level == 60) {
            heroBase.hpFull
        } else {
            val bonus = Math.floor((level - 1) * (heroBase.hpFull - heroBase.hpBase) / 59.0).toInt()
            heroBase.hpBase + bonus
        }
        baseArmor = if (this.level == 60) {
            heroBase.armorFull
        } else {
            val bonus = Math.floor((level - 1) * (heroBase.armorFull - heroBase.armorBase) / 59.0).toInt()
            heroBase.armorBase + bonus
        }
        baseInitiative = if (this.ascLvl >= 1) {
            this.heroBase.initiativeAsc
        } else {
            this.heroBase.initiative
        }
        baseCrit = if (this.ascLvl >= 1) {
            this.heroBase.critAsc
        } else {
            this.heroBase.crit
        }
        baseCritMult = if (this.ascLvl >= 1) {
            this.heroBase.critMultAsc
        } else {
            this.heroBase.critMult
        }
        baseDexterity = if (this.ascLvl >= 1) {
            this.heroBase.dexterityAsc
        } else {
            this.heroBase.dexterity
        }
        baseResistance = if (this.ascLvl >= 1) {
            this.heroBase.resistanceAsc
        } else {
            this.heroBase.resistance
        }

        sets = mutableListOf()
        
        strengthAbsBonus = 0
        strengthPercBonus = 0
        hpAbsBonus = 0
        hpPercBonus = 0
        armorAbsBonus = 0
        armorPercBonus = 0
        initiativeBonus = 0
        critBonus = 0
        critMultBonus = 0
        dexterityBonus = 0
        resistanceBonus = 0

        lifesteal = 0
        counterChance = 0
        reflect = 0
        evasionChance = 0
        speedBarFilling = 100
        armorPiercing = 0
        armorExtraDmg = 0
        healthExtraDmg = 0
        redDamageInc = 0
        greenDamageInc = 0
        blueDamageInc = 0
        healingInc = 0
        superCritChance = 0
        buffIntensityInc = 0
        debuffIntensityInc = 0
    }

    @PostConstruct
    @PostLoad
    fun calcStats() {
        initVars()
        getGears().forEach { it.apply(this) }
        GearSet.values().asList().map { set ->
            var completeSets = getGears().filter { it.set == set }.size / set.number
            while (completeSets > 0) {
                sets.add(set)
                completeSets --
            }
        }
        sets.forEach { it.apply(this) }
        //TODO passive skills
    }

    @JsonIgnore
    fun getGears(): List<Gear> {
        val gears = mutableListOf<Gear>()
        weapon?.let { gears.add(it) }
        shield?.let { gears.add(it) }
        helmet?.let { gears.add(it) }
        armor?.let { gears.add(it) }
        pants?.let { gears.add(it) }
        boots?.let { gears.add(it) }
        return gears
    }

    fun getGear(type: GearType): Gear? = when (type) {
        GearType.WEAPON -> this.weapon
        GearType.SHIELD -> this.shield
        GearType.HELMET -> this.helmet
        GearType.ARMOR -> this.armor
        GearType.PANTS -> this.pants
        GearType.BOOTS -> this.pants
    }

    fun equip(gear: Gear): Gear? {
        val unequipped = getGear(gear.type)
        unequipped?.equippedTo = null
        when (gear.type) {
            GearType.WEAPON -> this.weapon = gear
            GearType.SHIELD -> this.shield = gear
            GearType.HELMET -> this.helmet = gear
            GearType.ARMOR -> this.armor = gear
            GearType.PANTS -> this.pants = gear
            GearType.BOOTS -> this.pants = gear
        }
        gear.equippedTo = this.id
        this.calcStats()
        return unequipped
    }

    fun unequip(gearType: GearType): Gear? {
        val unequipped = getGear(gearType)
        unequipped?.equippedTo = null
        when (gearType) {
            GearType.WEAPON -> this.weapon = null
            GearType.SHIELD -> this.shield = null
            GearType.HELMET -> this.helmet = null
            GearType.ARMOR -> this.armor = null
            GearType.PANTS -> this.pants = null
            GearType.BOOTS -> this.pants = null
        }
        this.calcStats()
        return unequipped
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

}
