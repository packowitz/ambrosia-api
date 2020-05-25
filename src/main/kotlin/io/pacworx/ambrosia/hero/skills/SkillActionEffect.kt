package io.pacworx.ambrosia.hero.skills

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.battle.Buff
import io.pacworx.ambrosia.hero.skills.SkillActionType.*
import io.pacworx.ambrosia.hero.HeroStat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class SkillActionEffect(val type: SkillActionType,
                             val needTarget: Boolean = true,
                             val needDuration: Boolean = false,
                             val description: String,
                             @field:JsonIgnore val buff: Buff? = null,
                             @field:JsonIgnore val stat: HeroStat? = null) {
    STRENGTH_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with strength"),
    ARMOR_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with current armor"),
    ARMOR_MAX_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with max armor"),
    HP_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with current hitpoints"),
    HP_MAX_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with max hitpoints"),
    DEXTERITY_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with dexterity"),
    RESISTANCE_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with resistance"),
    HERO_LVL_SCALING(ADD_BASE_DMG, false, description = "Adds base damage scaling with hero level"),
    DMG_MULTIPLIER(ADD_BASE_DMG, false, description = "Adds percentage base damage. E.g. 10 for +10% damage"),
    FIXED_DMG(ADD_BASE_DMG, false, description = "Adds fixed base damage"),

    DEAL_PERCENTAGE(DEAL_DAMAGE, true, description = "Triggers dealing damage for the given percentage. 100 for full damage"),

    ARMOR_BUFF(BUFF, needDuration = true, description = "Grants Armor Buff with value intensity for duration turn", buff = Buff.ARMOR_BUFF),
    COUNTERATTACK_BUFF(BUFF, needDuration = true, description = "Grants Counterattack Buff with value intensity for duration turn", buff = Buff.COUNTERATTACK),
    CRIT_BUFF(BUFF, needDuration = true, description = "Grants Crit Buff with value intensity for duration turn", buff = Buff.CRIT_BUFF),
    CRIT_MULT_BUFF(BUFF, needDuration = true, description = "Grants CritMult Buff with value intensity for duration turn", buff = Buff.CRIT_MULT_BUFF),
    DEATHSHIELD_BUFF(BUFF, needDuration = true, description = "Grants Deathshield Buff with value intensity for duration turn", buff = Buff.DEATHSHIELD),
    DODGE_BUFF(BUFF, needDuration = true, description = "Grants Dodge Buff with value intensity for duration turn", buff = Buff.DODGE_BUFF),
    HASTE_BUFF(BUFF, needDuration = true, description = "Grants Haste Buff with value intensity for duration turn", buff = Buff.HASTE),
    HOT_BUFF(BUFF, needDuration = true, description = "Grants HoT Buff with value intensity for duration turn", buff = Buff.HEAL_OVER_TIME),
    STRENGTH_BUFF(BUFF, needDuration = true, description = "Grants Strength Buff with value intensity for duration turn", buff = Buff.STRENGTH_BUFF),
    TAUNT_BUFF(BUFF, needDuration = true, description = "Grants Taunt Buff with value intensity for duration turn", buff = Buff.TAUNT_BUFF),

    CONFUSE_DEBUFF(DEBUFF, needDuration = true, description = "Applies Confuse Debuff with value intensity for duration turn", buff = Buff.CONFUSE),
    CRIT_DEBUFF(DEBUFF, needDuration = true, description = "Applies Crit Debuff with value intensity for duration turn", buff = Buff.CRIT_DEBUFF),
    CRIT_MULT_DEBUFF(DEBUFF, needDuration = true, description = "Applies CritMult Debuff with value intensity for duration turn", buff = Buff.CRIT_MULT_DEBUFF),
    DOT_DEBUFF(DEBUFF, needDuration = true, description = "Applies DoT Debuff with value intensity for duration turn", buff = Buff.DAMAGE_OVER_TIME),
    HEAL_BLOCK_DEBUFF(DEBUFF, needDuration = true, description = "Applies HealBlock Debuff with value intensity for duration turn", buff = Buff.HEAL_BLOCK),
    SLOW_DEBUFF(DEBUFF, needDuration = true, description = "Applies Slow Debuff with value intensity for duration turn", buff = Buff.SLOW),
    STUN_DEBUFF(DEBUFF, needDuration = true, description = "Applies Stun Debuff with value intensity for duration turn", buff = Buff.STUN),
    WEAK_DEBUFF(DEBUFF, needDuration = true, description = "Applies Weak Debuff with value intensity for duration turn", buff = Buff.WEAK),

    PERCENTAGE(SPEEDBAR, description = "Fills speedbar by value percentage (-100 - +100)"),
    PERCENTAGE_MAX(SPEEDBAR, description = "Fills speedbar by value percentage of MAX speedbar (-100 - +100)"),

    TARGET_MAX_HP(HEAL, description = "Heals target(s) based on targets max HP by value percentage (1-100)"),
    OWN_MAX_HP(HEAL, description = "Heals target(s) based on active heros max HP by value percentage (1-100)"),

    STRENGTH_PASSIVE(PASSIVE_STAT, description = "Increases passively strength by value percentage (1-100)", stat = HeroStat.STRENGTH_PERC),
    ARMOR_PASSIVE(PASSIVE_STAT, description = "Increases passively armor by value percentage (1-100)", stat = HeroStat.ARMOR_PERC),
    CRIT_PASSIVE(PASSIVE_STAT, description = "Increases passively crit by value", stat = HeroStat.CRIT),
    CRIT_MULT_PASSIVE(PASSIVE_STAT, description = "Increases passively crit mult by value", stat = HeroStat.CRIT_MULT),
    DEXTERITY_MULT_PASSIVE(PASSIVE_STAT, description = "Increases passively dexterity by value", stat = HeroStat.DEXTERITY),
    RESISTANCE_PASSIVE(PASSIVE_STAT, description = "Increases passively resistance by value", stat = HeroStat.RESISTANCE),
    LIFESTEAL_PASSIVE(PASSIVE_STAT, description = "Increases passively lifesteal by value", stat = HeroStat.LIFESTEAL),
    COUNTER_PASSIVE(PASSIVE_STAT, description = "Increases passively chance to counter by value", stat = HeroStat.COUNTER_CHANCE),
    REFLECT_PASSIVE(PASSIVE_STAT, description = "Increases passively percentage of reflect damage by value", stat = HeroStat.REFLECT),
    DODGE_PASSIVE(PASSIVE_STAT, description = "Increases passively chance to dodge by value", stat = HeroStat.DODGE_CHANCE),
    SPEED_PASSIVE(PASSIVE_STAT, description = "Increases passively speed by value", stat = HeroStat.SPEED),
    ARMOR_PIERCING_PASSIVE(PASSIVE_STAT, description = "Increases passively armor piercing by value", stat = HeroStat.ARMOR_PIERCING),
    ARMOR_EXTRA_DMG_PASSIVE(PASSIVE_STAT, description = "Increases passively damage against armor by value", stat = HeroStat.ARMOR_EXTRA_DMG),
    HEALTH_EXTRA_DMG_PASSIVE(PASSIVE_STAT, description = "Increases passively damage after armor by value", stat = HeroStat.HEALTH_EXTRA_DMG),
    RED_DMG_INC_PASSIVE(PASSIVE_STAT, description = "Increases passively damage against red by value", stat = HeroStat.RED_DMG_INC),
    GREEN_DMG_INC_PASSIVE(PASSIVE_STAT, description = "Increases passively damage against green by value", stat = HeroStat.GREEN_DMG_INC),
    BLUE_DMG_INC_PASSIVE(PASSIVE_STAT, description = "Increases passively damage against blue by value", stat = HeroStat.BLUE_DMG_INC),
    HEALING_INC_PASSIVE(PASSIVE_STAT, description = "Increases passively received healing by value percentage (1-100)", stat = HeroStat.HEALING_INC),
    SUPER_CRIT_PASSIVE(PASSIVE_STAT, description = "Increases passively chance to super crit by value", stat = HeroStat.SUPER_CRIT_CHANCE),
    BUFF_INTENSITY_PASSIVE(PASSIVE_STAT, description = "Increases passively the intensity of all casted buffs by value", stat = HeroStat.BUFF_INTENSITY_INC),
    DEBUFF_INTENSITY_PASSIVE(PASSIVE_STAT, description = "Increases passively the intensity of all casted debuffs by value", stat = HeroStat.DEBUFF_INTENSITY_INC),
    BUFF_DURATION_PASSIVE(PASSIVE_STAT, description = "Increases passively the duration of all casted buffs by value", stat = HeroStat.BUFF_DURATION_INC),
    DEBUFF_DURATION_PASSIVE(PASSIVE_STAT, description = "Increases passively the duration of all casted debuffs by value", stat = HeroStat.DEBUFF_DURATION_INC),
    HEAL_PER_TURN_PASSIVE(PASSIVE_STAT, description = "Increases passively the percentage of self healing per turn by value", stat = HeroStat.HEAL_PER_TURN),
    DMG_PER_TURN_PASSIVE(PASSIVE_STAT, description = "Increases passively the percentage of self damage per turn by value", stat = HeroStat.DMG_PER_TURN),

    COOLDOWN(SPECIAL, needTarget = false, description = "Changes cooldown by given value. Eg -1 to reduce cooldown by 1"),
    INIT_COOLDOWN(SPECIAL, needTarget = false, description = "Changes initial cooldown by given value. Eg -1 to reduce initial cooldown by 1"),
    RESURRECT(SPECIAL, description = "Resurrects target with value percent of hitpoints"),
    REMOVE_BUFF(SPECIAL, description = "Removes one buff from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    REMOVE_ALL_BUFFS(SPECIAL, description = "Removes all buffs from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    REMOVE_DEBUFF(SPECIAL, description = "Removes one debuff from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    REMOVE_ALL_DEBUFFS(SPECIAL, description = "Removes all debuffs from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    SMALL_SHIELD(SPECIAL, needDuration = true, description = "Grants target(s) a small shield worth of 25% of his HP with value intensity for duration turns"),
    MEDIUM_SHIELD(SPECIAL, needDuration = true, description = "Grants target(s) a medium shield worth of 50% of his HP with value intensity for duration turns"),
    LARGE_SHIELD(SPECIAL, needDuration = true, description = "Grants target(s) a large shield worth of 100% of his HP with value intensity for duration turns")
    ;

    fun getName(): String = name
}
