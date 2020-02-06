package io.pacworx.ambrosia.properties

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.properties.PropertyCategory.*

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class PropertyType(@field:JsonFormat(shape = JsonFormat.Shape.STRING) val category: PropertyCategory, val description: String) {
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

    UPGRADE_TIME_BUILDING(BUILDING, "Defines how long (Value in seconds) an upgrade takes")
    ;

    fun getName(): String = name
}
