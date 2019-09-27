package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionType.*

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class SkillActionEffect(val type: SkillActionType, val description: String) {
    STRENGTH(DAMAGE, "Defines damage scaling with strength"),
    ARMOR(DAMAGE, "Defines damage scaling with current armor"),
    ARMOR_MAX(DAMAGE, "Defines damage scaling with max armor"),
    HP(DAMAGE, "Defines damage scaling with current hitpoints"),
    HP_MAX(DAMAGE, "Defines damage scaling with max hitpoints"),
    DEXTERITY(DAMAGE, "Defines damage scaling with dexterity"),
    RESISTANCE(DAMAGE, "Defines damage scaling with resistance"),
    MULTIPLIER(DAMAGE, "Adds percentage damage. E.g. 10 for +10% damage"),
    DEAL_PERCENTAGE(DEAL_DAMAGE, "Triggers dealing damage for the given percentage. 100 for full damage"),
    STRENGTH_BUFF(BUFF, "Grants Strength Buff with value intensity for duration turn"),
    ARMOR_BUFF(BUFF, "Grants Armor Buff with value intensity for duration turn"),
    TAUNT_BUFF(BUFF, "Grants Taunt Buff with value intensity for duration turn"),
    PERCENTAGE(SPEEDBAR, "Fills speedbar by value percentage (1-100)"),
    TARGET_MAX_HP(HEAL, "Heals target(s) based on targets max HP by value percentage (1-100)"),
    OWN_MAX_HP(HEAL, "Heals target(s) based on active heros max HP by value percentage (1-100)");

    fun getName(): String = name
}
