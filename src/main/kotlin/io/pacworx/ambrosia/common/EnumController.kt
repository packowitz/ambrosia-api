package io.pacworx.ambrosia.common

import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.battle.Buff
import io.pacworx.ambrosia.hero.skills.PassiveSkillTrigger
import io.pacworx.ambrosia.hero.skills.SkillActionEffect
import io.pacworx.ambrosia.hero.skills.SkillActionTarget
import io.pacworx.ambrosia.hero.skills.SkillActionTrigger
import io.pacworx.ambrosia.fights.stageconfig.SpeedBarChange
import io.pacworx.ambrosia.gear.GearSet
import io.pacworx.ambrosia.gear.GearType
import io.pacworx.ambrosia.gear.JewelType
import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.hero.HeroStat
import io.pacworx.ambrosia.hero.HeroType
import io.pacworx.ambrosia.hero.Rarity
import io.pacworx.ambrosia.hero.skills.SkillActionType
import io.pacworx.ambrosia.hero.skills.SkillActiveTrigger
import io.pacworx.ambrosia.hero.skills.SkillTarget
import io.pacworx.ambrosia.loot.LootItemType
import io.pacworx.ambrosia.maps.FightIcon
import io.pacworx.ambrosia.maps.MapBackground
import io.pacworx.ambrosia.maps.MapTileStructure
import io.pacworx.ambrosia.maps.MapTileType
import io.pacworx.ambrosia.progress.ProgressStat
import io.pacworx.ambrosia.properties.PropertyCategory
import io.pacworx.ambrosia.properties.PropertyType
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.story.StoryTrigger
import io.pacworx.ambrosia.upgrade.Modification
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType
import io.pacworx.ambrosia.vehicle.VehicleStat
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("enums")
class EnumController {

    @GetMapping
    fun getAllEnums(): Enums =
        Enums()

    data class Enums(
        val colors: List<Color> = Color.values().asList(),
        val heroTypes: List<HeroType> = HeroType.values().asList(),
        val rarities: List<Rarity> = Rarity.values().asList(),
        val skillActionEffects: List<SkillActionEffect> = SkillActionEffect.values().asList(),
        val skillActionTargets: List<SkillActionTarget> = SkillActionTarget.values().asList(),
        val skillActionTriggers: List<SkillActionTrigger> = SkillActionTrigger.values().asList(),
        val skillActionTypes: List<SkillActionType> = SkillActionType.values().asList(),
        val skillActiveTriggers: List<SkillActiveTrigger> = SkillActiveTrigger.values().asList(),
        val skillTargets: List<SkillTarget> = SkillTarget.values().asList(),
        val propertyCategories: List<PropertyCategory> = PropertyCategory.values().asList(),
        val propertyTypes: List<PropertyType> = PropertyType.values().asList(),
        val heroStats: List<HeroStat> = HeroStat.values().asList(),
        val gearSets: List<GearSet> = GearSet.values().asList(),
        val gearTypes: List<GearType> = GearType.values().asList(),
        val buffs: List<Buff> = Buff.values().asList(),
        val jewelTypes: List<JewelType> = JewelType.values().asList(),
        val passiveSkillTriggers: List<PassiveSkillTrigger> = PassiveSkillTrigger.values().asList(),
        val mapTileTypes: List<MapTileType> = MapTileType.values().asList(),
        val mapTileStructures: List<MapTileStructure> = MapTileStructure.values().asList(),
        val mapTileFightIcons: List<FightIcon> = FightIcon.values().asList(),
        val mapBackgrounds: List<MapBackground> = MapBackground.values().asList(),
        val fightConfigSpeedbarChanges: List<SpeedBarChange> = SpeedBarChange.values().asList(),
        val buildingTypes: List<BuildingType> = BuildingType.values().asList(),
        val resourceTypes: List<ResourceType> = ResourceType.values().asList(),
        val partQualities: List<PartQuality> = PartQuality.values().asList(),
        val partTypes: List<PartType> = PartType.values().asList(),
        val vehicleStats: List<VehicleStat> = VehicleStat.values().asList(),
        val lootItemTypes: List<LootItemType> = LootItemType.values().asList(),
        val modifications: List<Modification> = Modification.values().asList(),
        val storyTriggers: List<StoryTrigger> = StoryTrigger.values().asList(),
        val progressStats: List<ProgressStat> = ProgressStat.values().asList()
    )
}
