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
 */
enum class Buff(val type: BuffType) {
    STRENGTH_BUFF(BuffType.BUFF) {
        override fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
            propertyService.getProperties(PropertyType.STRENGTH_BUFF, buff.intensity).forEach {
                it.stat!!.apply(hero, it.value1)
            }
        }
    },
    ARMOR_BUFF(BuffType.BUFF) {
        override fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {
            propertyService.getProperties(PropertyType.ARMOR_BUFF, buff.intensity).forEach {
                it.stat!!.apply(hero, it.value1)
            }
        }
    };

    open fun preTurnAction(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {}

    open fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {}
}
