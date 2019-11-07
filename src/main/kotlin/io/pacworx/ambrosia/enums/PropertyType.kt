package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyCategory.*

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class PropertyType(val category: PropertyCategory, val description: String) {
    HP_ABS_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    HP_PERC_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    ARMOR_ABS_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    ARMOR_PERC_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    STRENGTH_ABS_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    STRENGTH_PERC_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    CRIT_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    CRIT_MULT_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    RESISTANCE_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    DEXTERITY_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    INITIATIVE_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    SPEED_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    STONE_SKIN_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    VITAL_AURA_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    POWER_FIST_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    BUFFS_BLESSING_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    BERSERKERS_AXE_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    MYTHICAL_MIRROR_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    WARHORN_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),
    REVERSED_REALITY_JEWEL(JEWEL, "Level: jewel level (1-10). Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a jewel. Value2 is ignored"),

    XP_MAX_HERO(HERO, "Level: hero level (1-60). Value1: max xp to reach next level. Stat and Value2 are ignored."),
    ASC_POINTS_MAX_HERO(HERO, "Level: hero ascension level (1-??). Value1: max asc points to reach next level. Stat and Value2 are ignored."),
    MERGE_XP_HERO(HERO, "Level: hero level (1-60) to be feeded. Value1: amount of xp gained. Stat and Value2 are ignored."),

    WEAPON_GEAR(GEAR, "Level: gear rarity (1-6). Stat: which stat the gear gives. Value1 and value2 defines the range for this stat."),
    SHIELD_GEAR(GEAR, "Level: gear rarity (1-6). Stat: which stat the gear gives. Value1 and value2 defines the range for this stat."),
    HELMET_GEAR(GEAR, "Level: gear rarity (1-6). Stat: which stat the gear gives. Value1 and value2 defines the range for this stat."),
    ARMOR_GEAR(GEAR, "Level: gear rarity (1-6). Stat: which stat the gear gives. Value1 and value2 defines the range for this stat."),
    PANTS_GEAR(GEAR, "Level: gear rarity (1-6). Stat: which stat the gear gives. Value1 and value2 defines the range for this stat."),
    BOOTS_GEAR(GEAR, "Level: gear rarity (1-6). Stat: which stat the gear gives. Value1 and value2 defines the range for this stat."),

    BATTLE_ARMOR(BATTLE, "Level: 100*dmg/armor threshold. Value1: Percentage (0-100) armor reduction. Value2: Percentage of damage (0-100) that is going through armor (health reduction). Stat is ignored."),

    STONE_SKIN_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    VITAL_AURA_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    POWER_FIST_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    BUFFS_BLESSING_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    BERSERKERS_AXE_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    MYTHICAL_MIRROR_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    WARHORN_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),
    REVERSED_REALITY_SET(SET, "Level: number of items to gain the bonus. Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set per level. Value2 are ignored."),

    STRENGTH_BUFF(BUFF, "Stat defines the granted bonus and level the intensity (1-5). Value2 is ignored."),
    ARMOR_BUFF(BUFF, "Stat defines the granted bonus and level the intensity (1-5). Value2 is ignored."),
    DOT_DEBUFF(BUFF, "Stat defines the granted bonus and level the intensity (1-5). Value2 is ignored."),
    HOT_BUFF(BUFF, "Stat defines the granted bonus and level the intensity (1-5). Value2 is ignored.");

    fun getName(): String = name
}
