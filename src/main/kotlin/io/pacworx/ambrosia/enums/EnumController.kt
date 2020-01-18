package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.*
import io.pacworx.ambrosia.io.pacworx.ambrosia.fights.stageconfig.SpeedBarChange
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.FightIcon
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapBackground
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapTileStructure
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.MapTileType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("enum")
class EnumController {

    @GetMapping("colors")
    fun getColors(): List<Color> = Color.values().asList()

    @GetMapping("hero_types")
    fun getHeroTypes(): List<HeroType> = HeroType.values().asList()

    @GetMapping("rarities")
    fun getRarities(): List<Rarity> = Rarity.values().asList()

    @GetMapping("skill_action_effects")
    fun getSkillActionEffects(): List<SkillActionEffect> = SkillActionEffect.values().asList()

    @GetMapping("skill_action_targets")
    fun getSkillActionTargets(): List<SkillActionTarget> = SkillActionTarget.values().asList()

    @GetMapping("skill_action_triggers")
    fun getSkillActionTriggers(): List<SkillActionTrigger> = SkillActionTrigger.values().asList()

    @GetMapping("skill_action_types")
    fun getSkillActionTypes(): List<SkillActionType> = SkillActionType.values().asList()

    @GetMapping("skill_active_triggers")
    fun getSkillActiveTriggers(): List<SkillActiveTrigger> = SkillActiveTrigger.values().asList()

    @GetMapping("skill_targets")
    fun getSkillTargets(): List<SkillTarget> = SkillTarget.values().asList()

    @GetMapping("property_categories")
    fun getPropertyCategories(): List<PropertyCategory> = PropertyCategory.values().asList()

    @GetMapping("property_types")
    fun getPropertyTypes(): List<PropertyType> = PropertyType.values().asList()

    @GetMapping("hero_stats")
    fun getHeroStats(): List<HeroStat> = HeroStat.values().asList()

    @GetMapping("gear_sets")
    fun getGearSets(): List<GearSet> = GearSet.values().asList()

    @GetMapping("jewel_types")
    fun getJewelTypes(): List<JewelType> = JewelType.values().asList()

    @GetMapping("passive_skill_triggers")
    fun getPassiveSkillTriggers():List<PassiveSkillTrigger> = PassiveSkillTrigger.values().asList()

    @GetMapping("map_tile_types")
    fun getMapTileTypes():List<MapTileType> = MapTileType.values().asList()

    @GetMapping("map_tile_structures")
    fun getMapTileStructures():List<MapTileStructure> = MapTileStructure.values().asList()

    @GetMapping("map_tile_fight_icons")
    fun getMapTileFights():List<FightIcon> = FightIcon.values().asList()

    @GetMapping("map_backgrounds")
    fun getMapBackgrounds():List<MapBackground> = MapBackground.values().asList()

    @GetMapping("fight_config_speedbar_changes")
    fun getFightConfigSpeedbarChanges():List<SpeedBarChange> = SpeedBarChange.values().asList()
}
