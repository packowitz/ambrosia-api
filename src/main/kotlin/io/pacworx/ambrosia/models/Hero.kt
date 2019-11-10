package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.GearType
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActiveTrigger
import io.pacworx.ambrosia.models.HeroBase
import javax.persistence.*
import kotlin.math.min

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
    var skill1: Int = 1,
    var skill2: Int? = null,
    var skill3: Int? = null,
    var skill4: Int? = null,
    var skill5: Int? = null,
    var skill6: Int? = null,
    var skill7: Int? = null,
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

    constructor(playerId: Long, heroBase: HeroBase) : this(
        playerId = playerId,
        heroBase = heroBase,
        stars = heroBase.rarity.stars,
        maxXp = 10000,
        ascPointsMax = 10000
    ) {
        heroBase.skills.filter { it.skillActiveTrigger == SkillActiveTrigger.ALWAYS }.forEach {
            when (it.number) {
                1 -> this.skill1 = 1
                2 -> this.skill2 = 1
                3 -> this.skill3 = 1
                4 -> this.skill4 = 1
                5 -> this.skill5 = 1
                6 -> this.skill6 = 1
                7 -> this.skill7 = 1
            }
        }
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
            GearType.BOOTS -> this.boots = gear
        }
        gear.equippedTo = this.id
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
        return unequipped
    }

    fun skillLevelUp(skillNumber: Int) {
        when (skillNumber) {
            1 -> this.skill1++
            2 -> this.skill2 = min(this.skill2?.inc() ?: 1, heroBase.skills.find { it.number == 2 }!!.maxLevel)
            3 -> this.skill3 = min(this.skill3?.inc() ?: 1, heroBase.skills.find { it.number == 3 }!!.maxLevel)
            4 -> this.skill4 = min(this.skill4?.inc() ?: 1, heroBase.skills.find { it.number == 4 }!!.maxLevel)
            5 -> this.skill5 = min(this.skill5?.inc() ?: 1, heroBase.skills.find { it.number == 5 }!!.maxLevel)
            6 -> this.skill6 = min(this.skill6?.inc() ?: 1, heroBase.skills.find { it.number == 6 }!!.maxLevel)
            7 -> this.skill7 = min(this.skill7?.inc() ?: 1, heroBase.skills.find { it.number == 7 }!!.maxLevel)
        }
    }

    fun skillLevelDown(skillNumber: Int) {
        when (skillNumber) {
            1 -> if (skill1 > 1) { this.skill1-- }
            2 -> this.skill2 = this.skill2?.dec()?.takeIf { it > 0 }
            3 -> this.skill3 = this.skill3?.dec()?.takeIf { it > 0 }
            4 -> this.skill4 = this.skill4?.dec()?.takeIf { it > 0 }
            5 -> this.skill5 = this.skill5?.dec()?.takeIf { it > 0 }
            6 -> this.skill6 = this.skill6?.dec()?.takeIf { it > 0 }
            7 -> this.skill7 = this.skill7?.dec()?.takeIf { it > 0 }
        }
    }

}
