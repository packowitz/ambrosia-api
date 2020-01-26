package io.pacworx.ambrosia.properties

import io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.gear.Gear
import io.pacworx.ambrosia.gear.HeroGearSet
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroSkillAction
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PropertyService(private val dynamicPropertyRepository: DynamicPropertyRepository) {

    private lateinit var properties: MutableList<DynamicProperty>

    @PostConstruct
    fun init() {
        properties = dynamicPropertyRepository.findAll()
    }

    fun getAllProperties(type: PropertyType): List<DynamicProperty> {
        return this.properties.filter { it.type == type }.sortedWith(compareBy({it.level}, {it.value1}))
    }

    fun getProperties(type: PropertyType, level: Int): List<DynamicProperty> {
        return this.properties.filter { it.type == type && it.level == level}
    }

    fun upsertProperties(type: PropertyType, properties: List<DynamicProperty>): List<DynamicProperty> {
        dynamicPropertyRepository.saveAll(properties)
        init()
        return getAllProperties(type)
    }

    fun deleteProperty(type: PropertyType, id: Long) {
        properties.find { it.type == type && it.id == id }?.let {
            dynamicPropertyRepository.delete(it)
        }
        init()
    }

    fun getHeroMaxXp(level: Int): Int {
        return properties.find { it.type == PropertyType.XP_MAX_HERO && it.level == level }?.value1
            ?: throw RuntimeException("No Max XP defined for hero level $level")
    }

    fun getHeroMaxAsc(level: Int): Int {
        return properties.find { it.type == PropertyType.ASC_POINTS_MAX_HERO && it.level == level }?.value1
            ?: throw RuntimeException("No Max Ascension points defined for asc level $level")
    }

    fun getPossibleGearStats(gearType: GearType, rarity: Rarity): List<HeroStat> {
        val propertyType = PropertyType.valueOf(gearType.name + "_GEAR")
        return properties
                .filter {
                    it.category == PropertyCategory.GEAR && it.type == propertyType && it.level == rarity.stars && it.stat != null
                }.map { it.stat!! }.distinct()
    }

    fun getGearValueRange(gearType: GearType, rarity: Rarity, stat: HeroStat): Pair<Int, Int> {
        val propertyType = PropertyType.valueOf(gearType.name + "_GEAR")
        return properties
                .find { it.category == PropertyCategory.GEAR && it.type == propertyType && it.level == rarity.stars && it.stat == stat }!!
                .let { it.value1 to it.value2!! }
    }

    fun applyBonuses(hero: HeroDto) {
        hero.getGears().forEach { applyGear(hero, it) }
        hero.sets = GearSet.values()
            .map { gearSet -> HeroGearSet(gearSet, hero.getGears().filter { it.set == gearSet }.size) }
            .filter { it.number > 0 }
        hero.sets.forEach { applySet(hero, it) }
        hero.heroBase.skills.filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.STAT_CALC }.forEach { skill ->
            skill.actions.filter { passiveActionTriggers(hero, it) }.forEach { action ->
                action.effect.stat?.apply(hero, action.effectValue)
            }
        }
    }

    fun applyGear(hero: HeroDto, gear: Gear) {
        when(gear.stat) {
            HeroStat.STRENGTH_ABS -> HeroStat.STRENGTH_ABS.apply(hero, gear.statValue)
            HeroStat.STRENGTH_PERC -> HeroStat.STRENGTH_PERC.apply(hero, gear.statValue)
            HeroStat.HP_ABS -> HeroStat.HP_ABS.apply(hero, gear.statValue)
            HeroStat.HP_PERC -> HeroStat.HP_PERC.apply(hero, gear.statValue)
            HeroStat.ARMOR_ABS -> HeroStat.ARMOR_ABS.apply(hero, gear.statValue)
            HeroStat.ARMOR_PERC -> HeroStat.ARMOR_PERC.apply(hero, gear.statValue)
            HeroStat.SPEED -> HeroStat.SPEED.apply(hero, gear.statValue)
            HeroStat.CRIT -> HeroStat.CRIT.apply(hero, gear.statValue)
            HeroStat.CRIT_MULT -> HeroStat.CRIT_MULT.apply(hero, gear.statValue)
            HeroStat.DEXTERITY -> HeroStat.DEXTERITY.apply(hero, gear.statValue)
            HeroStat.RESISTANCE -> HeroStat.RESISTANCE.apply(hero, gear.statValue)
        }
        gear.jewel1Type?.let { applyJewel(hero, it, gear.jewel1Level!!) }
        gear.jewel2Type?.let { applyJewel(hero, it, gear.jewel2Level!!) }
        gear.jewel3Type?.let { applyJewel(hero, it, gear.jewel3Level!!) }
        gear.jewel4Type?.let { applyJewel(hero, it, gear.jewel4Level!!) }
        gear.specialJewelType?.let { applyJewel(hero, it, gear.specialJewelLevel!!) }
    }

    fun applyJewel(hero: HeroDto, jewelType: JewelType, level: Int) {
        properties.find { it.type.name == jewelType.name + "_JEWEL" && it.level == level }?.let {
            it.stat?.apply(hero, it.value1)
        }
    }

    fun applySet(hero: HeroDto, set: HeroGearSet) {
        var setNumber = set.number
        val prop = mutableListOf<DynamicProperty>()
        while (setNumber > 0 && prop.isEmpty()) {
            prop.addAll(properties.filter { it.type.name == set.gearSet.name + "_SET" && it.level!! == set.number })
            setNumber --
        }
        prop.takeIf { it.isNotEmpty() }?.forEach {
            it.stat?.apply(hero, it.value1)
            if (set.description.isNotEmpty()) {
                set.description += " "
            }
            set.description = it.stat!!.desc(it.value1)
        }
    }

    private fun passiveActionTriggers(hero: HeroDto, action: HeroSkillAction): Boolean {
        return when (action.trigger) {
            SkillActionTrigger.ALWAYS -> true
            SkillActionTrigger.S1_LVL -> action.triggerValue!!.contains(hero.skill1.toString())
            SkillActionTrigger.S2_LVL -> hero.skill2?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S3_LVL -> hero.skill3?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S4_LVL -> hero.skill4?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S5_LVL -> hero.skill5?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S6_LVL -> hero.skill6?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            SkillActionTrigger.S7_LVL -> hero.skill7?.let { action.triggerValue!!.contains(it.toString()) } ?: false
            else -> false
        }
    }
}
