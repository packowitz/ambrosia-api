package io.pacworx.ambrosia.io.pacworx.ambrosia.services

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicProperty
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.DynamicPropertyRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Gear
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
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
        return properties.find { it.type == PropertyType.XP_MAX_HERO && it.level == level }?.let { it.value1 } ?: 10
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
        hero.sets.clear()
        GearSet.values().asList().map { set ->
            var completeSets = hero.getGears().filter { it.set == set }.size / set.pieces
            while (completeSets > 0) {
                hero.sets.add(set)
                completeSets --
            }
        }
        hero.sets.forEach { applySet(hero, it) }
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

    fun applySet(hero: HeroDto, set: GearSet) {
        properties.find { it.type.name == set.name + "_SET" }?.let {
            it.stat?.apply(hero, it.value1)
        }
    }
}
