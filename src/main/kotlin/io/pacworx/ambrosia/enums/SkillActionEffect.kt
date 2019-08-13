package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionType.BUFF
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionType.DAMAGE

enum class SkillActionEffect(type: SkillActionType, description: String) {
    STRENGTH(DAMAGE, "Defines damage scaling with strength"),
    ARMOR(DAMAGE, "Defines damage scaling with armor"),
    HP(DAMAGE, "Defines damage scaling with hitpoints"),
    DEXTERITY(DAMAGE, "Defines damage scaling with dexterity"),
    RESISTANCE(DAMAGE, "Defines damage scaling with resistance"),
    MULTIPLIER(DAMAGE, "Adds percentage damage. E.g. 0.1 for +10% damage"),
    STRENGTH_BUFF(BUFF, "Grants Strength Buff"),
    ARMOR_BUFF(BUFF, "Grants Armor Buff"),
    RESIST_BUFF(BUFF, "Grants Resist Buff"),
    DEX_BUFF(BUFF, "Grants Dex Buff")
}
