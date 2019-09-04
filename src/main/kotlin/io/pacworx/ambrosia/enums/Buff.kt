package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHero
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHeroBuff
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService

enum class Buff(val type: BuffType) {
    STRENGTH_BUFF(BuffType.BUFF),
    ARMOR_BUFF(BuffType.BUFF);

    open fun preTurnAction(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {}

    open fun applyEffect(battle: Battle, hero: BattleHero, buff: BattleHeroBuff, propertyService: PropertyService) {}
}
