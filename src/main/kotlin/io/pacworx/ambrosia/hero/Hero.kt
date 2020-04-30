package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.enums.GearType
import io.pacworx.ambrosia.enums.SkillActiveTrigger
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.hero.base.HeroBase
import javax.persistence.*
import kotlin.math.max

@Entity
data class Hero(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    var missionId: Long? = null,
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
    var skillPoints: Int = 0,
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
    @JoinColumn(name = "gloves_id")
    var gloves: Gear? = null,
    @OneToOne
    @JoinColumn(name = "boots_id")
    var boots: Gear? = null
) {

    constructor(playerId: Long, heroBase: HeroBase, maxXp: Int, ascPointsMax: Int, level: Int) : this(
        playerId = playerId,
        heroBase = heroBase,
        stars = max(heroBase.rarity.stars, (level % 10).takeIf { it > 0 }?.let { (level / 10) + 1 } ?: level / 10),
        level= level,
        maxXp = maxXp,
        ascPointsMax = ascPointsMax
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
        GearType.GLOVES -> this.gloves
        GearType.BOOTS -> this.boots
    }

    fun equip(gear: Gear): Gear? {
        val unequipped = getGear(gear.type)
        unequipped?.equippedTo = null
        when (gear.type) {
            GearType.WEAPON -> this.weapon = gear
            GearType.SHIELD -> this.shield = gear
            GearType.HELMET -> this.helmet = gear
            GearType.ARMOR -> this.armor = gear
            GearType.GLOVES -> this.gloves = gear
            GearType.BOOTS -> this.boots = gear
        }
        gear.equippedTo = this.id
        return unequipped
    }

    fun unequipAll(): List<Gear> = listOfNotNull(
        weapon?.also { unequip(it.type) },
        shield?.also { unequip(it.type) },
        helmet?.also { unequip(it.type) },
        armor?.also { unequip(it.type) },
        gloves?.also { unequip(it.type) },
        boots?.also { unequip(it.type) }
    )

    fun unequip(gearType: GearType): Gear? {
        val unequipped = getGear(gearType)
        unequipped?.equippedTo = null
        when (gearType) {
            GearType.WEAPON -> this.weapon = null
            GearType.SHIELD -> this.shield = null
            GearType.HELMET -> this.helmet = null
            GearType.ARMOR -> this.armor = null
            GearType.GLOVES -> this.gloves = null
            GearType.BOOTS -> this.boots = null
        }
        return unequipped
    }

    fun skillLevelUp(skillNumber: Int) {
        if (this.skillPoints <= 0) {
            throw RuntimeException("Hero has no available skill points")
        }
        when (skillNumber) {
            1 -> skill1.takeIf { it < this.heroBase.skills.find { it.number == 1 }!!.maxLevel }?.also {
                skill1 = it + 1
                skillPoints --
            }
            2 -> skill2?.takeIf { it < this.heroBase.skills.find { it.number == 2 }!!.maxLevel }?.also {
                skill2 = it + 1
                skillPoints --
            }
            3 -> skill3?.takeIf { it < this.heroBase.skills.find { it.number == 3 }!!.maxLevel }?.also {
                skill3 = it + 1
                skillPoints --
            }
            4 -> skill4?.takeIf { it < this.heroBase.skills.find { it.number == 4 }!!.maxLevel }?.also {
                skill4 = it + 1
                skillPoints --
            }
            5 -> skill5?.takeIf { it < this.heroBase.skills.find { it.number == 5 }!!.maxLevel }?.also {
                skill5 = it + 1
                skillPoints --
            }
            6 -> skill6?.takeIf { it < this.heroBase.skills.find { it.number == 6 }!!.maxLevel }?.also {
                skill6 = it + 1
                skillPoints --
            }
            7 -> skill7?.takeIf { it < this.heroBase.skills.find { it.number == 7 }!!.maxLevel }?.also {
                skill7 = it + 1
                skillPoints --
            }
        }
    }

    fun resetSkills() {
        skillPoints += skill1 - 1
        skill1 = 1
        skill2 = skill2?.let { skillPoints += it - 1; 1 }
        skill3 = skill3?.let { skillPoints += it - 1; 1 }
        skill4 = skill4?.let { skillPoints += it - 1; 1 }
        skill5 = skill5?.let { skillPoints += it - 1; 1 }
        skill6 = skill6?.let { skillPoints += it - 1; 1 }
        skill7 = skill7?.let { skillPoints += it - 1; 1 }
    }

}
