package io.pacworx.ambrosia.battle

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.properties.PropertyService
import io.pacworx.ambrosia.properties.PropertyType

/**
 * To add a new Buff you need to
 * - add the buff to this enum
 * - add a property type to PropertyType
 * - optional: add dynamic properties as db migration to define the effect bonus
 * - add buff to SkillActionEffect
 * - add icon to UI
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class Buff(
    val type: BuffType,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING) val propertyType: PropertyType,
    val description: String
) {
    ARMOR_BUFF(BuffType.BUFF, PropertyType.ARMOR_BUFF, "Increases hero's armor"),
    COUNTERATTACK(BuffType.BUFF, PropertyType.COUNTERATTACK_BUFF, "Increases the possibility to counter an attack"),
    CRIT_BUFF(BuffType.BUFF, PropertyType.CRIT_BUFF, "Increases hero's chance to land a critical hit"),
    CRIT_MULT_BUFF(BuffType.BUFF, PropertyType.CRIT_MULT_BUFF, "Increases hero's damage multiplier for critical hits"),
    DODGE_BUFF(BuffType.BUFF, PropertyType.DODGE_BUFF, "Increases hero's chance to dodge an attack"),
    HASTE(BuffType.BUFF, PropertyType.HASTE_BUFF, "Increases hero's speed"),
    DEATHSHIELD(BuffType.BUFF, PropertyType.DEATHSHIELD_BUFF, "Hero cannot die (stay at 1 HP lowest)"),
    HEAL_OVER_TIME(BuffType.BUFF, PropertyType.HOT_BUFF, "Healing the hero in the beginning of his turn"),
    SHIELD(BuffType.BUFF, PropertyType.SHIELD_BUFF, "Shield absorbs incoming damage (before armor)"),
    STRENGTH_BUFF(BuffType.BUFF, PropertyType.STRENGTH_BUFF, "Increases hero's strength"),
    TAUNT_BUFF(BuffType.BUFF, PropertyType.TAUNT_BUFF, "Taunting heroes must be attacked first"),

    CONFUSE(BuffType.DEBUFF, PropertyType.CONFUSE_DEBUFF, "Confused heroes attack a random ally if there is one alive otherwise their turn is skipped.") {
        override fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
            super.applyEffect(battle, hero, buff, propertyService)
            if (hero.status != HeroStatus.DEAD && hero.status != HeroStatus.STUNNED) {
                hero.status = HeroStatus.CONFUSED
            }
        }
    },
    CRIT_DEBUFF(BuffType.DEBUFF, PropertyType.CRIT_DEBUFF, "Reducing hero's chance to land a critical hit"),
    CRIT_MULT_DEBUFF(BuffType.DEBUFF, PropertyType.CRIT_MULT_DEBUFF, "Reducing hero's damage multiplier for critical hits"),
    DAMAGE_OVER_TIME(BuffType.DEBUFF, PropertyType.DOT_DEBUFF, "Dealing damage to the hero in the beginning of his turn"),
    HEAL_BLOCK(BuffType.DEBUFF, PropertyType.HEAL_BLOCK_DEBUFF, "Reducing any healing the hero receives"),
    SLOW(BuffType.DEBUFF, PropertyType.SLOW_DEBUFF, "Reducing hero's speed"),
    STUN(BuffType.DEBUFF, PropertyType.STUN_DEBUFF, "Stunned heroes are skipping their turn") {
        override fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
            super.applyEffect(battle, hero, buff, propertyService)
            if (hero.status != HeroStatus.DEAD) {
                hero.status = HeroStatus.STUNNED
            }
        }
    },
    WEAK(BuffType.DEBUFF, PropertyType.WEAK_DEBUFF, "Decreases hero's strength");

    fun getBuffName(): String = name

    open fun initTurnEffect(hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
        propertyService.getProperties(propertyType, buff.intensity).forEach {
            it.stat?.initTurn(hero, it.value1)
        }
    }

    open fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
        propertyService.getProperties(propertyType, buff.intensity).forEach {
            it.stat?.apply(hero, it.value1)
        }
    }
}
