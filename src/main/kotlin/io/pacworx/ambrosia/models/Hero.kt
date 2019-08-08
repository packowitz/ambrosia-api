package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.GearType
import io.pacworx.ambrosia.models.HeroBase
import javax.persistence.*

@Entity
data class Hero(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
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
    @JoinColumn(name = "bracers_id")
    var bracers: Gear? = null,
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

    constructor(playerId: Long, heroBase: HeroBase) : this(
        playerId = playerId,
        heroBase = heroBase,
        stars = heroBase.rarity.stars,
        maxXp = 10000,
        ascPointsMax = 10000
    )

    fun getGear(type: GearType): Gear? = when (type) {
        GearType.WEAPON -> this.weapon
        GearType.BRACERS -> this.bracers
        GearType.HELMET -> this.helmet
        GearType.ARMOR -> this.armor
        GearType.PANTS -> this.pants
        GearType.BOOTS -> this.pants
    }

    fun equip(gear: Gear): Gear? {
        val unequipped = getGear(gear.type)?.let {
            it.equippedTo = null
            it
        }
        when (gear.type) {
            GearType.WEAPON -> this.weapon = gear
            GearType.BRACERS -> this.bracers = gear
            GearType.HELMET -> this.helmet = gear
            GearType.ARMOR -> this.armor = gear
            GearType.PANTS -> this.pants = gear
            GearType.BOOTS -> this.pants = gear
        }
        gear.equippedTo = this.id
        return unequipped
    }

    fun getBaseStrength(): Int {
        if (this.level == 60) {
            return this.heroBase.strengthFull
        }
        val bonus = Math.floor((level - 1) * (heroBase.strengthFull - heroBase.strengthBase) / 59.0).toInt()
        return this.heroBase.strengthBase + bonus
    }

    fun getBaseHp(): Int {
        if (this.level == 60) {
            return this.heroBase.hpFull
        }
        val bonus = Math.floor((level - 1) * (heroBase.hpFull - heroBase.hpBase) / 59.0).toInt()
        return this.heroBase.hpBase + bonus
    }

    fun getBaseArmor(): Int {
        if (this.level == 60) {
            return this.heroBase.armorFull
        }
        val bonus = Math.floor((level - 1) * (heroBase.armorFull - heroBase.armorBase) / 59.0).toInt()
        return this.heroBase.armorBase + bonus
    }

    fun getBaseSpeed(): Int {
        return if (this.ascLvl >= 1) {
            this.heroBase.speedAsc
        } else {
            this.heroBase.speed
        }
    }

    fun getBaseCrit(): Int {
        return if (this.ascLvl >= 1) {
            this.heroBase.critAsc
        } else {
            this.heroBase.crit
        }
    }

    fun getBaseCritMult(): Int {
        return if (this.ascLvl >= 1) {
            this.heroBase.critMultAsc
        } else {
            this.heroBase.critMult
        }
    }

    fun getBaseDexterity(): Int {
        return if (this.ascLvl >= 1) {
            this.heroBase.dexterityAsc
        } else {
            this.heroBase.dexterity
        }
    }

    fun getBaseResistance(): Int {
        return if (this.ascLvl >= 1) {
            this.heroBase.resistanceAsc
        } else {
            this.heroBase.resistance
        }
    }
}