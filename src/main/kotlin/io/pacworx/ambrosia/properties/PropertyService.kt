package io.pacworx.ambrosia.properties

import io.pacworx.ambrosia.buildings.BuildingRepository
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.gear.*
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroStat
import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.hero.skills.HeroSkill
import io.pacworx.ambrosia.hero.skills.HeroSkillAction
import io.pacworx.ambrosia.hero.skills.PassiveSkillTrigger
import io.pacworx.ambrosia.hero.skills.SkillActionTrigger
import io.pacworx.ambrosia.resources.ResourcesRepository
import io.pacworx.ambrosia.resources.ResourcesService
import org.springframework.stereotype.Service

@Service
class PropertyService(
    private val dynamicPropertyRepository: DynamicPropertyRepository,
    private val gearRepository: GearRepository,
    private val resourcesRepository: ResourcesRepository,
    private val resourcesService: ResourcesService,
    private val buildingRepository: BuildingRepository,
    private val heroRepository: HeroRepository,
    private val propertyVersionRepository: PropertyVersionRepository
) {

    private val cache: MutableMap<Pair<PropertyType, Int>, List<DynamicProperty>> = mutableMapOf()
    private val propertyVersions: MutableList<PropertyVersion> = mutableListOf()

    fun getVersion(type: PropertyType): PropertyVersion {
        return propertyVersions.find { it.propertyType == type }
            ?: (propertyVersionRepository.findByPropertyTypeAndActiveIsTrue(type)
                ?: propertyVersionRepository.save(PropertyVersion(propertyType = type, version = 1, active = true))).also { propertyVersions.add(it) }
    }

    fun getProperties(typeAndVersion: Pair<PropertyType, Int>): List<DynamicProperty> {
        return cache.getOrPut(typeAndVersion, { dynamicPropertyRepository.findAllByTypeAndVersionOrderByLevelAscValue1Asc(typeAndVersion.first, typeAndVersion.second) })
    }

    fun getProperties(type: PropertyType): List<DynamicProperty> {
        return getProperties(Pair(type, getVersion(type).version))
    }

    fun getProperties(type: PropertyType, level: Int): List<DynamicProperty> {
        return getProperties(type).filter { it.level == level }
    }

    fun upsertProperties(type: PropertyType, properties: List<DynamicProperty>): List<DynamicProperty> {
        val prevProps = getProperties(type)
        if (type.category == PropertyCategory.GEAR) {
            // if gear values have changed, existing gear needs to recalc it's stat
            prevProps.forEach { prevProp ->
                val newProp = properties.find { it.level == prevProp.level && it.stat == prevProp.stat }
                if (newProp != null) {
                    if (newProp.value2 == null) {
                        throw RuntimeException("Gear properties require a second value")
                    }
                    if (newProp.value1 != prevProp.value1 || newProp.value2 != prevProp.value2) {
                        val gearType = GearType.valueOf(type.name.substringBefore("_GEAR"))
                        val rarity =  Rarity.values().find { it.stars == newProp.level }!!
                        gearRepository.findAllByTypeAndRarityAndStat(gearType, rarity, prevProp.stat!!).forEach { gear ->
                            gear.statValue = newProp.value1 + ((gear.statQuality * (newProp.value2 - newProp.value1)) / 100)
                        }
                    }
                }
            }
        } else if (type == PropertyType.STORAGE_BUILDING) {
            resourcesRepository.findAll().forEach { resources ->
                val storageLvl = buildingRepository.findByPlayerIdAndType(resources.playerId, BuildingType.STORAGE)?.level ?: 0
                var appliedLvl = 0
                resources.resetMaxValues()
                while (appliedLvl < storageLvl) {
                    appliedLvl ++
                    properties
                        .filter { it.level == appliedLvl && it.resourceType != null && it.resourceType.name.endsWith("_MAX") }
                        .forEach {
                            resourcesService.gainResources(resources, it.resourceType!!, it.value1)
                        }
                }
            }
        } else if (type == PropertyType.XP_MAX_HERO) {
            prevProps.forEach { prevProp ->
                val newProp = properties.find { it.level == prevProp.level && it.stat == prevProp.stat }
                if (newProp != null) {
                    if (newProp.value1 != prevProp.value1) {
                        heroRepository.updateMaxXp(newProp.level!!, newProp.value1)
                    }
                }
            }
        } else if (type == PropertyType.ASC_POINTS_MAX_HERO) {
            prevProps.forEach { prevProp ->
                val newProp = properties.find { it.level == prevProp.level && it.stat == prevProp.stat }
                if (newProp != null) {
                    if (newProp.value1 != prevProp.value1) {
                        heroRepository.updateMaxAsc(newProp.level!!, newProp.value1)
                    }
                }
            }
        }

        val prevVersion = propertyVersions.find { it.propertyType == type }?.let {
            it.active = false
            propertyVersionRepository.save(it)
        }
        val version = propertyVersionRepository.save(PropertyVersion(
            propertyType = type,
            version = (prevVersion?.version ?: 1) + 1,
            active = true
        ))
        propertyVersions.removeIf { it.propertyType == type }
        val newProps = properties.map {
            DynamicProperty(it, version.version)
        }
        val props = dynamicPropertyRepository.saveAll(newProps)
        cache.remove(Pair(type, version.version))
        return props
    }

    fun getPlayerMaxXp(level: Int): Int {
        return getProperties(PropertyType.XP_MAX_PLAYER, level).takeIf { it.isNotEmpty() }?.first()?.value1
            ?: throw RuntimeException("No Max XP defined for player level $level")
    }

    fun getHeroMaxXp(level: Int): Int {
        return getProperties(PropertyType.XP_MAX_HERO, level).takeIf { it.isNotEmpty() }?.first()?.value1
            ?: throw RuntimeException("No Max XP defined for hero level $level")
    }

    fun getHeroMergedXp(heroLevel: Int): Int {
        return getProperties(PropertyType.MERGE_XP_HERO, heroLevel).takeIf { it.isNotEmpty() }?.first()?.value1
            ?: throw RuntimeException("No Merge XP defined for hero level $heroLevel")
    }

    fun getHeroMaxAsc(level: Int): Int {
        return getProperties(PropertyType.ASC_POINTS_MAX_HERO, level).takeIf { it.isNotEmpty() }?.first()?.value1
            ?: throw RuntimeException("No Max Ascension points defined for asc level $level")
    }

    fun getHeroMergedAsc(rarity: Int): Int {
        return getProperties(PropertyType.MERGE_ASC_HERO, rarity).takeIf { it.isNotEmpty() }?.first()?.value1
            ?: throw RuntimeException("No Merge Ascension points defined for rarity $rarity")
    }

    fun getPossibleGearStats(gearType: GearType, rarity: Rarity): List<HeroStat> {
        val propertyType = PropertyType.valueOf(gearType.name + "_GEAR")
        return getProperties(propertyType, rarity.stars).mapNotNull { it.stat }.distinct()
    }

    fun getGearValueRange(gearType: GearType, rarity: Rarity, stat: HeroStat): Pair<Int, Int> {
        val propertyType = PropertyType.valueOf(gearType.name + "_GEAR")
        return getProperties(propertyType, rarity.stars).find { it.stat == stat }!!.let { it.value1 to it.value2!! }
    }

    fun applyBonuses(hero: HeroDto) {
        hero.getGears().forEach { applyGear(hero, it) }
        hero.sets = GearSet.values()
            .map { gearSet -> HeroGearSet(gearSet, hero.getGears().filter { it.set == gearSet }.size) }
            .filter { it.number > 0 }
        hero.sets.forEach { applySet(hero, it) }
        hero.heroBase.skills
            .filter { it.passive && it.passiveSkillTrigger == PassiveSkillTrigger.STAT_CALC && hero.getSkillLevel(it.number) > 0 }
            .forEach { skill ->
                skill.actions.filter { passiveActionTriggers(hero, skill, it) }.forEach { action ->
                    action.effect.stat?.apply(hero, action.effectValue)
                }
            }
    }

    fun applyGear(hero: HeroDto, gear: Gear) {
        gear.stat.apply(hero, gear.statValue)
        gear.jewel1Type?.let { applyJewel(hero, it, gear.jewel1Level!!) }
        gear.jewel2Type?.let { applyJewel(hero, it, gear.jewel2Level!!) }
        gear.jewel3Type?.let { applyJewel(hero, it, gear.jewel3Level!!) }
        gear.jewel4Type?.let { applyJewel(hero, it, gear.jewel4Level!!) }
        gear.specialJewelType?.let { applyJewel(hero, it, gear.specialJewelLevel!!) }
    }

    fun applyJewel(hero: HeroDto, jewelType: JewelType, level: Int) {
        val type = PropertyType.valueOf(jewelType.name + "_JEWEL")
        getProperties(type, level).forEach { it.stat?.apply(hero, it.value1) }
    }

    fun applySet(hero: HeroDto, set: HeroGearSet) {
        var setNumber = set.number
        val prop = mutableListOf<DynamicProperty>()
        val type = PropertyType.valueOf(set.gearSet.name + "_SET")
        while (setNumber > 0 && prop.isEmpty()) {
            prop.addAll(getProperties(type, setNumber))
            setNumber --
        }
        prop.forEach {
            it.stat?.apply(hero, it.value1)
            if (set.description.isNotEmpty()) {
                set.description += " "
            }
            set.description += it.stat!!.desc(it.value1)
        }
    }

    private fun passiveActionTriggers(hero: HeroDto, skill: HeroSkill, action: HeroSkillAction): Boolean {
        return when (action.trigger) {
            SkillActionTrigger.ALWAYS -> true
            SkillActionTrigger.SKILL_LVL -> triggerValueSkillLevel(action.triggerValue!!, hero.getSkillLevel(skill.number))
            SkillActionTrigger.S1_LVL -> triggerValueSkillLevel(action.triggerValue!!, hero.skill1)
            SkillActionTrigger.S2_LVL -> hero.skill2?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S3_LVL -> hero.skill3?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S4_LVL -> hero.skill4?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S5_LVL -> hero.skill5?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S6_LVL -> hero.skill6?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            SkillActionTrigger.S7_LVL -> hero.skill7?.let { triggerValueSkillLevel(action.triggerValue!!, it) } ?: false
            else -> false
        }
    }

    private fun triggerValueSkillLevel(triggerValue: String, skillLevel: Int): Boolean {
        return if (triggerValue.startsWith(">=")) {
            skillLevel >= triggerValue.substring(2).trim().toIntOrNull() ?: 99
        } else if (triggerValue.startsWith(">")) {
            skillLevel > triggerValue.substring(1).trim().toIntOrNull() ?: 99
        } else if (triggerValue.startsWith("<=")) {
            skillLevel <= 0 && skillLevel < triggerValue.substring(2).trim().toIntOrNull() ?: 99
        } else if (triggerValue.startsWith("<")) {
            skillLevel < 0 && skillLevel <= triggerValue.substring(1).trim().toIntOrNull() ?: 99
        } else {
            triggerValue.contains(skillLevel.toString())
        }
    }
}
