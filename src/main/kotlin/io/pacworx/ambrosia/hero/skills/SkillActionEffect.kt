package io.pacworx.ambrosia.hero.skills

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.battle.Buff
import io.pacworx.ambrosia.hero.skills.SkillActionType.*
import io.pacworx.ambrosia.hero.HeroStat

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class SkillActionEffect(val type: SkillActionType,
                             val description: String,
                             @field:JsonIgnore val buff: Buff? = null,
                             @field:JsonIgnore val stat: HeroStat? = null) {
    STRENGTH(DAMAGE, "Defines damage scaling with strength"),
    ARMOR(DAMAGE, "Defines damage scaling with current armor"),
    ARMOR_MAX(DAMAGE, "Defines damage scaling with max armor"),
    HP(DAMAGE, "Defines damage scaling with current hitpoints"),
    HP_MAX(DAMAGE, "Defines damage scaling with max hitpoints"),
    DEXTERITY(DAMAGE, "Defines damage scaling with dexterity"),
    RESISTANCE(DAMAGE, "Defines damage scaling with resistance"),
    MULTIPLIER(DAMAGE, "Adds percentage damage. E.g. 10 for +10% damage"),

    DEAL_PERCENTAGE(DEAL_DAMAGE, "Triggers dealing damage for the given percentage. 100 for full damage"),

    ARMOR_BUFF(BUFF, "Grants Armor Buff with value intensity for duration turn", Buff.ARMOR_BUFF),
    COUNTERATTACK_BUFF(BUFF, "Grants Counterattack Buff with value intensity for duration turn", Buff.COUNTERATTACK),
    CRIT_BUFF(BUFF, "Grants Crit Buff with value intensity for duration turn", Buff.COUNTERATTACK),
    CRIT_MULT_BUFF(BUFF, "Grants CritMult Buff with value intensity for duration turn", Buff.COUNTERATTACK),
    DEATHSHIELD_BUFF(BUFF, "Grants Deathshield Buff with value intensity for duration turn", Buff.COUNTERATTACK),
    DODGE_BUFF(BUFF, "Grants Dodge Buff with value intensity for duration turn", Buff.COUNTERATTACK),
    HASTE_BUFF(BUFF, "Grants Haste Buff with value intensity for duration turn", Buff.HEAL_OVER_TIME),
    HOT_BUFF(BUFF, "Grants HoT Buff with value intensity for duration turn", Buff.HEAL_OVER_TIME),
    STRENGTH_BUFF(BUFF, "Grants Strength Buff with value intensity for duration turn", Buff.STRENGTH_BUFF),
    TAUNT_BUFF(BUFF, "Grants Taunt Buff with value intensity for duration turn", Buff.TAUNT_BUFF),

    CONFUSE_DEBUFF(DEBUFF, "Applies Confuse Debuff with value intensity for duration turn", Buff.CONFUSE),
    CRIT_DEBUFF(DEBUFF, "Applies Crit Debuff with value intensity for duration turn", Buff.CONFUSE),
    CRIT_MULT_DEBUFF(DEBUFF, "Applies CritMult Debuff with value intensity for duration turn", Buff.CONFUSE),
    DOT_DEBUFF(DEBUFF, "Applies DoT Debuff with value intensity for duration turn", Buff.DAMAGE_OVER_TIME),
    HEAL_BLOCK_DEBUFF(DEBUFF, "Applies HealBlock Debuff with value intensity for duration turn", Buff.HEAL_BLOCK),
    SLOW_DEBUFF(DEBUFF, "Applies Slow Debuff with value intensity for duration turn", Buff.STUN),
    STUN_DEBUFF(DEBUFF, "Applies Stun Debuff with value intensity for duration turn", Buff.STUN),
    WEAK_DEBUFF(DEBUFF, "Applies Weak Debuff with value intensity for duration turn", Buff.WEAK),

    PERCENTAGE(SPEEDBAR, "Fills speedbar by value percentage (-100 - +100)"),
    PERCENTAGE_MAX(SPEEDBAR, "Fills speedbar by value percentage of MAX speedbar (-100 - +100)"),

    TARGET_MAX_HP(HEAL, "Heals target(s) based on targets max HP by value percentage (1-100)"),
    OWN_MAX_HP(HEAL, "Heals target(s) based on active heros max HP by value percentage (1-100)"),

    STRENGTH_PASSIVE(PASSIVE_STAT, "Increases passively strength by value percentage (1-100)", stat = HeroStat.STRENGTH_PERC),
    ARMOR_PASSIVE(PASSIVE_STAT, "Increases passively armor by value percentage (1-100)", stat = HeroStat.ARMOR_PERC),
    CRIT_PASSIVE(PASSIVE_STAT, "Increases passively crit by value", stat = HeroStat.CRIT),
    CRIT_MULT_PASSIVE(PASSIVE_STAT, "Increases passively crit mult by value", stat = HeroStat.CRIT_MULT),
    DEXTERITY_MULT_PASSIVE(PASSIVE_STAT, "Increases passively dexterity by value", stat = HeroStat.DEXTERITY),
    RESISTANCE_PASSIVE(PASSIVE_STAT, "Increases passively resistance by value", stat = HeroStat.RESISTANCE),
    LIFESTEAL_PASSIVE(PASSIVE_STAT, "Increases passively lifesteal by value", stat = HeroStat.LIFESTEAL),
    COUNTER_PASSIVE(PASSIVE_STAT, "Increases passively chance to counter by value", stat = HeroStat.COUNTER_CHANCE),
    REFLECT_PASSIVE(PASSIVE_STAT, "Increases passively percentage of reflect damage by value", stat = HeroStat.REFLECT),
    DODGE_PASSIVE(PASSIVE_STAT, "Increases passively chance to dodge by value", stat = HeroStat.DODGE_CHANCE),
    SPEED_PASSIVE(PASSIVE_STAT, "Increases passively speed by value", stat = HeroStat.SPEED),
    ARMOR_PIERCING_PASSIVE(PASSIVE_STAT, "Increases passively armor piercing by value", stat = HeroStat.ARMOR_PIERCING),
    ARMOR_EXTRA_DMG_PASSIVE(PASSIVE_STAT, "Increases passively damage against armor by value", stat = HeroStat.ARMOR_EXTRA_DMG),
    HEALTH_EXTRA_DMG_PASSIVE(PASSIVE_STAT, "Increases passively damage after armor by value", stat = HeroStat.HEALTH_EXTRA_DMG),
    RED_DMG_INC_PASSIVE(PASSIVE_STAT, "Increases passively damage against red by value", stat = HeroStat.RED_DMG_INC),
    GREEN_DMG_INC_PASSIVE(PASSIVE_STAT, "Increases passively damage against green by value", stat = HeroStat.GREEN_DMG_INC),
    BLUE_DMG_INC_PASSIVE(PASSIVE_STAT, "Increases passively damage against blue by value", stat = HeroStat.BLUE_DMG_INC),
    HEALING_INC_PASSIVE(PASSIVE_STAT, "Increases passively received healing by value percentage (1-100)", stat = HeroStat.HEALING_INC),
    SUPER_CRIT_PASSIVE(PASSIVE_STAT, "Increases passively chance to super crit by value", stat = HeroStat.SUPER_CRIT_CHANCE),
    BUFF_INTENSITY_PASSIVE(PASSIVE_STAT, "Increases passively the intensity of all casted buffs by value", stat = HeroStat.BUFF_INTENSITY_INC),
    DEBUFF_INTENSITY_PASSIVE(PASSIVE_STAT, "Increases passively the intensity of all casted debuffs by value", stat = HeroStat.DEBUFF_INTENSITY_INC),
    BUFF_DURATION_PASSIVE(PASSIVE_STAT, "Increases passively the duration of all casted buffs by value", stat = HeroStat.BUFF_DURATION_INC),
    DEBUFF_DURATION_PASSIVE(PASSIVE_STAT, "Increases passively the duration of all casted debuffs by value", stat = HeroStat.DEBUFF_DURATION_INC),
    HEAL_PER_TURN_PASSIVE(PASSIVE_STAT, "Increases passively the percentage of self healing per turn by value", stat = HeroStat.HEAL_PER_TURN),
    DMG_PER_TURN_PASSIVE(PASSIVE_STAT, "Increases passively the percentage of self damage per turn by value", stat = HeroStat.DMG_PER_TURN),

    COOLDOWN(SPECIAL, "Changes cooldown by given value. Eg -1 to reduce cooldown by 1"),
    INIT_COOLDOWN(SPECIAL, "Changes initial cooldown by given value. Eg -1 to reduce initial cooldown by 1"),
    RESURRECT(SPECIAL, "Resurrects target with value percent of hitpoints"),
    REMOVE_BUFF(SPECIAL, "Removes one buff from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    REMOVE_ALL_BUFFS(SPECIAL, "Removes all buffs from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    REMOVE_DEBUFF(SPECIAL, "Removes one debuff from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    REMOVE_ALL_DEBUFFS(SPECIAL, "Removes all debuffs from target(s); value 1 means that this action cannot be resisted; put value 0 for normal resistance calculation"),
    SMALL_SHIELD(SPECIAL, "Grants target(s) a small shield worth of 25% of his HP with value intensity for duration turns"),
    MEDIUM_SHIELD(SPECIAL, "Grants target(s) a medium shield worth of 50% of his HP with value intensity for duration turns"),
    LARGE_SHIELD(SPECIAL, "Grants target(s) a large shield worth of 100% of his HP with value intensity for duration turns")
    ;

    fun getName(): String = name
}
