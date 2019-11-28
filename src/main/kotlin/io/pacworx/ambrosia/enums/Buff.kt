package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHero
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHeroBuff
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService

/**
 * To add a new Buff you need to
 * - add the buff to this enum and implement the effect
 * - add dynamic properties to define the effect bonus
 * - add buff to SkillActionEffect
 * - add icon to UI
 */
enum class Buff(val type: BuffType, val propertyType: PropertyType) {
    ARMOR_BUFF(BuffType.BUFF, PropertyType.ARMOR_BUFF),
    HEAL_OVER_TIME(BuffType.BUFF, PropertyType.HOT_BUFF),
    SHIELD(BuffType.BUFF, PropertyType.SHIELD_BUFF),
    STRENGTH_BUFF(BuffType.BUFF, PropertyType.STRENGTH_BUFF),
    TAUNT_BUFF(BuffType.BUFF, PropertyType.TAUNT_BUFF),

    DAMAGE_OVER_TIME(BuffType.DEBUFF, PropertyType.DOT_DEBUFF);

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
