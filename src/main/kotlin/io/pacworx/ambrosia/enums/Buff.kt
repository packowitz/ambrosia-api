package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHero
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHeroBuff
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.HeroStatus
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService

/**
 * To add a new Buff you need to
 * - add the buff to this enum
 * - add a property type to PropertyType
 * - optional: add dynamic properties as db migration to define the effect bonus
 * - add buff to SkillActionEffect
 * - add icon to UI
 */
enum class Buff(val type: BuffType, val propertyType: PropertyType) {
    ARMOR_BUFF(BuffType.BUFF, PropertyType.ARMOR_BUFF),
    HEAL_OVER_TIME(BuffType.BUFF, PropertyType.HOT_BUFF),
    SHIELD(BuffType.BUFF, PropertyType.SHIELD_BUFF),
    STRENGTH_BUFF(BuffType.BUFF, PropertyType.STRENGTH_BUFF),
    TAUNT_BUFF(BuffType.BUFF, PropertyType.TAUNT_BUFF),

    DAMAGE_OVER_TIME(BuffType.DEBUFF, PropertyType.DOT_DEBUFF),
    STUN(BuffType.DEBUFF, PropertyType.STUN_DEBUFF) {
        override fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
            super.applyEffect(battle, hero, buff, propertyService)
            if (hero.status != HeroStatus.DEAD) {
                hero.status = HeroStatus.STUNNED
            }
        }
    };

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
