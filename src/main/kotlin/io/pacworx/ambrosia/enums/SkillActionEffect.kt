package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionType.*

enum class SkillActionEffect(type: SkillActionType, description: String) {
    STRENGTH(DAMAGE, "Defines damage scaling with strength"),
    ARMOR(DAMAGE, "Defines damage scaling with current armor"),
    ARMOR_MAX(DAMAGE, "Defines damage scaling with max armor"),
    HP(DAMAGE, "Defines damage scaling with current hitpoints"),
    HP_MAX(DAMAGE, "Defines damage scaling with max hitpoints"),
    DEXTERITY(DAMAGE, "Defines damage scaling with dexterity"),
    RESISTANCE(DAMAGE, "Defines damage scaling with resistance"),
    MULTIPLIER(DAMAGE, "Adds percentage damage. E.g. 10 for +10% damage"),
    DEAL_PERCENTAGE(DEAL_DAMAGE, "Triggers dealing damage for the given percentage. 100 for full damage"),
    STRENGTH_BUFF(BUFF, "Grants Strength Buff"),
    ARMOR_BUFF(BUFF, "Grants Armor Buff"),
    RESIST_BUFF(BUFF, "Grants Resist Buff"),
    DEX_BUFF(BUFF, "Grants Dex Buff"),
    PERCENTAGE(SPEEDBAR, "Fills speedbar by given percentage"),
    TARGET_MAX_HP(HEAL, "Heals based on targets max HP"),
    OWN_MAX_HP(HEAL, "Heals based on own max HP")
}
