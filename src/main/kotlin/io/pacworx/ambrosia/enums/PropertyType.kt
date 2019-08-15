package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.PropertyCategory.*

enum class PropertyType(category: PropertyCategory, description: String) {
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

    XP_MAX_HERO(HERO, "Level: hero level (1-60). Value1: max xp to reach next level. Stat and Value2 are ignored."),
    ASC_POINTS_MAX_HERO(HERO, "Level: hero ascension level (1-??). Value1: max asc points to reach next level. Stat and Value2 are ignored."),
    MERGE_XP_HERO(HERO, "Level: hero level (1-60) to be feeded. Value1: amount of xp gained. Stat and Value2 are ignored."),

    HP_ABS_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    HP_PERC_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    ARMOR_ABS_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    ARMOR_PERC_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    STRENGTH_ABS_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    STRENGTH_PERC_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    CRIT_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    CRIT_MULT_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    RESISTANCE_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    DEXTERITY_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    INITIATIVE_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),
    SPEED_GEAR(GEAR, "Level: gear rarity (1-6). Value1 and value2 defines the range for this stat. Stat is ignored."),

    BATTLE_ARMOR(BATTLE, "Level: 100*dmg/armor threshold. Value1: Percentage (0-100) armor reduction. Value2: Percentage of damage (0-100) that is going through armor (health reduction). Stat is ignored."),

    STONE_SKIN_SET(SET, "Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set. Level and Value2 are ignored."),
    VITAL_AURA_SET(SET, "Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set. Level and Value2 are ignored."),
    POWER_FIST_SET(SET, "Stat defines the bonus and value1 how high that bonus is. You can have multiple bonuses on a set. Level and Value2 are ignored.")
}
