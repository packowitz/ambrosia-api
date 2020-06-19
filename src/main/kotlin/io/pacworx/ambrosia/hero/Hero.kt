package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.exceptions.InsufficientResourcesException
import io.pacworx.ambrosia.gear.GearType
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.hero.skills.SkillActiveTrigger
import io.pacworx.ambrosia.player.Player
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
    var markedAsBoss: Boolean = false,
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

    fun skillLevelUp(player: Player, skillNumber: Int) {
        if (this.skillPoints <= 0) {
            throw InsufficientResourcesException(player.id, "skill points", 1)
        }
        heroBase.getSkill(skillNumber)?.let { skill ->
            val currentSkillLevel = getSkillLevel(skillNumber)
                ?: if (skill.skillActiveTrigger == SkillActiveTrigger.NPC_ONLY && player.serviceAccount) { 0 }
                else { null }
            if (currentSkillLevel != null && currentSkillLevel < skill.maxLevel) {
                setSkillLevel(skillNumber, currentSkillLevel + 1)
                skillPoints --
            }
        }
    }

    fun enableSkill(skillNumber: Int) {
        when (skillNumber) {
            2 -> if (skill2 ?: 0 <= 0) { skill2 = 1 }
            3 -> if (skill3 ?: 0 <= 0) { skill3 = 1 }
            4 -> if (skill4 ?: 0 <= 0) { skill4 = 1 }
            5 -> if (skill5 ?: 0 <= 0) { skill5 = 1 }
            6 -> if (skill6 ?: 0 <= 0) { skill6 = 1 }
            7 -> if (skill7 ?: 0 <= 0) { skill7 = 1 }
        }
    }

    fun disableSkill(skillNumber: Int) {
        setSkillLevel(skillNumber, null)
    }

    fun resetSkills() {
        skill1 = 1
        skill2 = minSkillLevel(2)
        skill3 = minSkillLevel(3)
        skill4 = minSkillLevel(4)
        skill5 = minSkillLevel(5)
        skill6 = minSkillLevel(6)
        skill7 = minSkillLevel(7)
        skillPoints = ascLvl
    }

    private fun minSkillLevel(skillNumber: Int): Int? {
        return heroBase.getSkill(skillNumber)
            ?.takeIf {
                it.skillActiveTrigger != SkillActiveTrigger.NPC_ONLY
                        && (it.skillActiveTrigger == SkillActiveTrigger.ALWAYS || ascLvl > 0)
            }?.let { 1 }
    }

    fun recheckSkillLevels(movedSkills: List<Pair<Int, Int>>?) {
        movedSkills?.takeIf { it.isNotEmpty() }?.let { m ->
            val new1 = m.find { it.second == 1 }?.let { getSkillLevel(it.first) ?: 1 } ?: skill1
            val new2 = if (m.any { it.second == 2 }) (m.find { it.second == 2 }?.let { getSkillLevel(it.first) }) else { skill2 }
            val new3 = if (m.any { it.second == 3 }) (m.find { it.second == 3 }?.let { getSkillLevel(it.first) }) else { skill3 }
            val new4 = if (m.any { it.second == 4 }) (m.find { it.second == 4 }?.let { getSkillLevel(it.first) }) else { skill4 }
            val new5 = if (m.any { it.second == 5 }) (m.find { it.second == 5 }?.let { getSkillLevel(it.first) }) else { skill5 }
            val new6 = if (m.any { it.second == 6 }) (m.find { it.second == 6 }?.let { getSkillLevel(it.first) }) else { skill6 }
            val new7 = if (m.any { it.second == 7 }) (m.find { it.second == 7 }?.let { getSkillLevel(it.first) }) else { skill7 }
            skill1 = new1
            skill2 = new2
            skill3 = new3
            skill4 = new4
            skill5 = new5
            skill6 = new6
            skill7 = new7
        }
        heroBase.skills.find { it.number == 1 }?.let { skill ->
            if (skill1 > skill.maxLevel) {
                skillPoints += (skill.maxLevel - skill1)
                skill1 = skill.maxLevel
            }
        }

        (2..7).forEach { skillNumber ->
            heroBase.skills.find { it.number == skillNumber }?.let { skill ->
                if (skill.skillActiveTrigger == SkillActiveTrigger.NPC_ONLY
                    || (skill.skillActiveTrigger == SkillActiveTrigger.ASCENDED && ascLvl == 0)) {
                    if (getSkillLevel(skill.number) ?: 0 > 1) {
                        skillPoints += getSkillLevel(skill.number)!! - 1
                    }
                    disableSkill(skill.number)
                } else {
                    ensureMaxLevel(skillNumber, skill.maxLevel)
                }
            } ?: ensureMaxLevel(skillNumber, null)
        }
    }

    private fun ensureMaxLevel(skillNumber: Int, maxLevel: Int?) {
        if (maxLevel != null) {
            getSkillLevel(skillNumber)?.takeIf { it > 0 }?.let { skillLevel ->
                if (skillLevel > maxLevel) {
                    skillPoints += (maxLevel - skillLevel)
                    setSkillLevel(skillNumber, maxLevel)
                }
            } ?: run { setSkillLevel(skillNumber, 1) }
        } else {
            skillPoints += getSkillLevel(skillNumber) ?: 0
            disableSkill(skillNumber)
        }
    }

    private fun getSkillLevel(skillNumber: Int): Int? {
        return when (skillNumber) {
            1 -> skill1
            2 -> skill2
            3 -> skill3
            4 -> skill4
            5 -> skill5
            6 -> skill6
            7 -> skill7
            else -> null
        }
    }

    private fun setSkillLevel(skillNumber: Int, level: Int?) {
        when (skillNumber) {
            1 -> skill1 = level!!
            2 -> skill2 = level
            3 -> skill3 = level
            4 -> skill4 = level
            5 -> skill5 = level
            6 -> skill6 = level
            7 -> skill7 = level
        }
    }

}
