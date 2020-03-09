package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.properties.PropertyCategory.*
import io.pacworx.ambrosia.vehicle.PartQuality
import io.pacworx.ambrosia.vehicle.PartType

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class PropertyType(
    @field:JsonFormat(shape = JsonFormat.Shape.STRING) val category: PropertyCategory,
    val description: String,
    val partType: PartType? = null,
    val partQuality: PartQuality? = null) {
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
    STORAGE_RESOURCES(RESOURCES, "Level: Building level. Resources stored in storage."),

    UPGRADE_TIME_BUILDING(BUILDING, "Defines how long (Value in seconds) an upgrade takes"),

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