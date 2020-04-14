package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.buildings.BuildingType
import io.pacworx.ambrosia.properties.PropertyCategory.*
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class PropertyType(
    @field:JsonFormat(shape = JsonFormat.Shape.STRING) val category: PropertyCategory,
    val description: String,
    val partType: PartType? = null,
    val partQuality: PartQuality? = null,
    val buildingType: BuildingType? = null,
    val showStat: Boolean = false,
    val showResources: Boolean = false,
    val showVehicleStat: Boolean = false,
    val showValue2: Boolean = false,
    val value1name: String? = null,
    val value2name: String? = null
) {
    HP_ABS_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    HP_PERC_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    ARMOR_ABS_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    ARMOR_PERC_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    STRENGTH_ABS_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    STRENGTH_PERC_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    CRIT_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    CRIT_MULT_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    RESISTANCE_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    DEXTERITY_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    INITIATIVE_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    SPEED_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    STONE_SKIN_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    VITAL_AURA_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    POWER_FIST_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    BUFFS_BLESSING_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    BERSERKERS_AXE_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    MYTHICAL_MIRROR_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    WARHORN_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    REVERSED_REALITY_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    MARK_1_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    MARK_2_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    MARK_3_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    MARK_4_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),
    MARK_5_JEWEL(JEWEL, "Jewel level (1-10). Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a jewel."),

    XP_MAX_HERO(HERO, "Level: hero level (1-60). Value: max xp to reach next level."),
    ASC_POINTS_MAX_HERO(HERO, "Level: hero ascension level (1-??). Value: max asc points to reach next level."),
    MERGE_XP_HERO(HERO, "Level: hero level (1-60) to be feeded. Value: amount of xp gained."),
    MERGE_ASC_HERO(HERO, "Level: Rarity (1-6). Value: Asc points gained when merging hero of same class"),

    WEAPON_GEAR(GEAR, "Gear rarity (1-6). Stat: which stat the gear gives. From - To defines the range for this stat."),
    SHIELD_GEAR(GEAR, "Gear rarity (1-6). Stat: which stat the gear gives. From - To defines the range for this stat."),
    HELMET_GEAR(GEAR, "Gear rarity (1-6). Stat: which stat the gear gives. From - To defines the range for this stat."),
    ARMOR_GEAR(GEAR, "Gear rarity (1-6). Stat: which stat the gear gives. From - To defines the range for this stat."),
    PANTS_GEAR(GEAR, "Gear rarity (1-6). Stat: which stat the gear gives. From - To defines the range for this stat."),
    BOOTS_GEAR(GEAR, "Gear rarity (1-6). Stat: which stat the gear gives. From - To defines the range for this stat."),

    BATTLE_ARMOR(BATTLE, "Threshold: 100*dmg/armor. ArmorReduction in Percentage (0-100). HealthReduction: Percentage of damage (0-100) that is going through armor."),

    STONE_SKIN_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    VITAL_AURA_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    POWER_FIST_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    BUFFS_BLESSING_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    BERSERKERS_AXE_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    MYTHICAL_MIRROR_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    WARHORN_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    REVERSED_REALITY_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    MARK_1_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    MARK_2_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    MARK_3_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    MARK_4_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),
    MARK_5_SET(SET, "NoGear: number of items to gain the bonus. Stat defines the bonus and amount how high that bonus is. You can have multiple bonuses on a set per level."),

    ARMOR_BUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    COUNTERATTACK_BUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    HOT_BUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    STRENGTH_BUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),

    DEATHSHIELD_BUFF(BUFF, "Intensity 1-5. Please select only BUFF_RESISTANCE as bonus."),
    SHIELD_BUFF(BUFF, "Intensity 1-5. Please select only BUFF_RESISTANCE as bonus."),
    TAUNT_BUFF(BUFF, "Intensity 1-5. Please select only BUFF_RESISTANCE as bonus."),

    CONFUSE_DEBUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    DOT_DEBUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    HEAL_BLOCK_DEBUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    STUN_DEBUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),
    WEAK_DEBUFF(BUFF, "Intensity 1-5. Stat defines the granted bonus."),

    PLAYER_LVL_RESOURCES(RESOURCES, "Resources gained on Player level up. Level 1 is starting level"),

    ACADEMY_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.ACADEMY, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    ARENA_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.ARENA, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    BARRACKS_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.BARRACKS, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    BAZAAR_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.BAZAAR, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    FORGE_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.FORGE, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    GARAGE_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.GARAGE, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    JEWELRY_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.JEWELRY, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    LABORATORY_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.LABORATORY, description = "Defines how long (in seconds) an upgrade to the level given takes"),
    STORAGE_UP_TIME(category = UPGRADE_TIME, buildingType = BuildingType.STORAGE, description = "Defines how long (in seconds) an upgrade to the given level takes"),
    VEHICLE_0_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle with no special plugs to the given level"),
    VEHICLE_1_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle with one special plug to the given level"),
    VEHICLE_2_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle with two special plugs to the given level"),
    VEHICLE_3_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle with three special plugs to the given level"),
    PART_BASIC_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle part of basic quality"),
    PART_MODERATE_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle part of moderate quality"),
    PART_GOOD_UP_TIME(category = UPGRADE_TIME, description = "Defines how long (in seconds) it takes to upgrade a vehicle part of good quality"),

    ACADEMY_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.ACADEMY, description = "Defines the cost an upgrade to the given level costs"),
    ARENA_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.ARENA, description = "Defines the cost an upgrade to the given level costs"),
    BARRACKS_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.BARRACKS, description = "Defines the cost an upgrade to the given level costs"),
    BAZAAR_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.BAZAAR, description = "Defines the cost an upgrade to the given level costs"),
    FORGE_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.FORGE, description = "Defines the cost an upgrade to the given level costs"),
    GARAGE_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.GARAGE, description = "Defines the cost an upgrade to the given level costs"),
    JEWELRY_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.JEWELRY, description = "Defines the cost an upgrade to the given level costs"),
    LABORATORY_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.LABORATORY, description = "Defines the cost an upgrade to the given level costs"),
    STORAGE_UP_COST(category = UPGRADE_COST, buildingType = BuildingType.STORAGE, description = "Defines the cost an upgrade to the given level costs"),
    VEHICLE_0_UP_COST(category = UPGRADE_COST, description = "Defines how much an vehicle upgrade with no special plugs costs"),
    VEHICLE_1_UP_COST(category = UPGRADE_COST, description = "Defines how much an vehicle upgrade with one special plug costs"),
    VEHICLE_2_UP_COST(category = UPGRADE_COST, description = "Defines how much an vehicle upgrade with two special plugs costs"),
    VEHICLE_3_UP_COST(category = UPGRADE_COST, description = "Defines how much an vehicle upgrade with three special plugs costs"),
    PART_BASIC_UP_COST(category = UPGRADE_COST, description = "Defines how much it costs to upgrade a vehicle part of basic quality to the given level"),
    PART_MODERATE_UP_COST(category = UPGRADE_COST, description = "Defines how much it costs to upgrade a vehicle part of moderate quality to the given level"),
    PART_GOOD_UP_COST(category = UPGRADE_COST, description = "Defines how much it costs to upgrade a vehicle part of good quality to the given level"),

    BARRACKS_BUILDING(category = BUILDING, description = "Defines the increase of barrack's capacity for reaching each level."),
    STORAGE_BUILDING(category = BUILDING, description = "Defines the increase of storage's capacity for reaching each level for each max resource.", showResources = true),
    ACADEMY_BUILDING(category = BUILDING, description = "Defines the max level of a hero that can be trained and evolved in the academy", value1name = "MaxTrainLvl"),
    GARAGE_BUILDING(category = BUILDING, description = "Defines the max level of a hero that can be trained and evolved in the academy", value1name = "VehicleStorage", showValue2 = true, value2name = "PartsStorage"),

    ENGINE_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.ENGINE, partQuality = PartQuality.BASIC),
    ENGINE_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.ENGINE, partQuality = PartQuality.MODERATE),
    ENGINE_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.ENGINE, partQuality = PartQuality.GOOD),
    FRAME_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.FRAME, partQuality = PartQuality.BASIC),
    FRAME_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.FRAME, partQuality = PartQuality.MODERATE),
    FRAME_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.FRAME, partQuality = PartQuality.GOOD),
    COMPUTER_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.COMPUTER, partQuality = PartQuality.BASIC),
    COMPUTER_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.COMPUTER, partQuality = PartQuality.MODERATE),
    COMPUTER_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.COMPUTER, partQuality = PartQuality.GOOD),
    SMOKE_BOMB_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.SMOKE_BOMB, partQuality = PartQuality.BASIC),
    SMOKE_BOMB_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.SMOKE_BOMB, partQuality = PartQuality.MODERATE),
    SMOKE_BOMB_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.SMOKE_BOMB, partQuality = PartQuality.GOOD),
    RAILGUN_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.RAILGUN, partQuality = PartQuality.BASIC),
    RAILGUN_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.RAILGUN, partQuality = PartQuality.MODERATE),
    RAILGUN_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.RAILGUN, partQuality = PartQuality.GOOD),
    SNIPER_SCOPE_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.SNIPER_SCOPE, partQuality = PartQuality.BASIC),
    SNIPER_SCOPE_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.SNIPER_SCOPE, partQuality = PartQuality.MODERATE),
    SNIPER_SCOPE_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.SNIPER_SCOPE, partQuality = PartQuality.GOOD),
    MEDI_KIT_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MEDI_KIT, partQuality = PartQuality.BASIC),
    MEDI_KIT_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MEDI_KIT, partQuality = PartQuality.MODERATE),
    MEDI_KIT_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MEDI_KIT, partQuality = PartQuality.GOOD),
    REPAIR_KIT_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.REPAIR_KIT, partQuality = PartQuality.BASIC),
    REPAIR_KIT_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.REPAIR_KIT, partQuality = PartQuality.MODERATE),
    REPAIR_KIT_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.REPAIR_KIT, partQuality = PartQuality.GOOD),
    EXTRA_ARMOR_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.EXTRA_ARMOR, partQuality = PartQuality.BASIC),
    EXTRA_ARMOR_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.EXTRA_ARMOR, partQuality = PartQuality.MODERATE),
    EXTRA_ARMOR_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.EXTRA_ARMOR, partQuality = PartQuality.GOOD),
    NIGHT_VISION_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.NIGHT_VISION, partQuality = PartQuality.BASIC),
    NIGHT_VISION_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.NIGHT_VISION, partQuality = PartQuality.MODERATE),
    NIGHT_VISION_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.NIGHT_VISION, partQuality = PartQuality.GOOD),
    MAGNETIC_SHIELD_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MAGNETIC_SHIELD, partQuality = PartQuality.BASIC),
    MAGNETIC_SHIELD_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MAGNETIC_SHIELD, partQuality = PartQuality.MODERATE),
    MAGNETIC_SHIELD_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MAGNETIC_SHIELD, partQuality = PartQuality.GOOD),
    MISSILE_DEFENSE_PART_BASIC(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MISSILE_DEFENSE, partQuality = PartQuality.BASIC),
    MISSILE_DEFENSE_PART_MODERATE(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MISSILE_DEFENSE, partQuality = PartQuality.MODERATE),
    MISSILE_DEFENSE_PART_GOOD(category = VEHICLE, description = "Defines the bonus for the level of the vehicle part", partType = PartType.MISSILE_DEFENSE, partQuality = PartQuality.GOOD)
    ;

    fun getName(): String = name
}
