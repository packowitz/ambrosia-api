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
    STRENGTH_BUFF_1(BUFF, "Grants 1* Strength Buff"),
    STRENGTH_BUFF_2(BUFF, "Grants 2* Strength Buff"),
    STRENGTH_BUFF_3(BUFF, "Grants 3* Strength Buff"),
    STRENGTH_BUFF_4(BUFF, "Grants 4* Strength Buff"),
    STRENGTH_BUFF_5(BUFF, "Grants 5* Strength Buff"),
    ARMOR_BUFF_1(BUFF, "Grants 1* Armor Buff"),
    ARMOR_BUFF_2(BUFF, "Grants 2* Armor Buff"),
    ARMOR_BUFF_3(BUFF, "Grants 3* Armor Buff"),
    ARMOR_BUFF_4(BUFF, "Grants 4* Armor Buff"),
    ARMOR_BUFF_5(BUFF, "Grants 5* Armor Buff"),
    RESIST_BUFF_1(BUFF, "Grants 1* Resist Buff"),
    RESIST_BUFF_2(BUFF, "Grants 2* Resist Buff"),
    RESIST_BUFF_3(BUFF, "Grants 3* Resist Buff"),
    RESIST_BUFF_4(BUFF, "Grants 4* Resist Buff"),
    RESIST_BUFF_5(BUFF, "Grants 5* Resist Buff"),
    DEX_BUFF_1(BUFF, "Grants 1* Dex Buff"),
    DEX_BUFF_2(BUFF, "Grants 2* Dex Buff"),
    DEX_BUFF_3(BUFF, "Grants 3* Dex Buff"),
    DEX_BUFF_4(BUFF, "Grants 4* Dex Buff"),
    DEX_BUFF_5(BUFF, "Grants 5* Dex Buff")
}