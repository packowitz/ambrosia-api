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
enum class Buff(val type: BuffType, val propertyType: PropertyType? = null) {
    STRENGTH_BUFF(BuffType.BUFF, PropertyType.STRENGTH_BUFF),
    ARMOR_BUFF(BuffType.BUFF, PropertyType.ARMOR_BUFF),
    TAUNT_BUFF(BuffType.BUFF),
    DAMAGE_OVER_TIME(BuffType.DEBUFF, PropertyType.DOT_DEBUFF),
    HEAL_OVER_TIME(BuffType.BUFF, PropertyType.HOT_BUFF);

    open fun preTurnAction(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {}

    open fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
        propertyType?.let {
            propertyService.getProperties(propertyType, buff.intensity).forEach {
                it.stat?.apply(hero, it.value1)
            }
        }
    }
}
