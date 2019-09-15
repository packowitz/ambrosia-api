package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import java.lang.RuntimeException

enum class HeroPosition {
    NONE,
    HERO1 { override fun getBattleHero(battle: Battle): BattleHero = battle.hero1!! },
    HERO2 { override fun getBattleHero(battle: Battle): BattleHero = battle.hero2!! },
    HERO3 { override fun getBattleHero(battle: Battle): BattleHero = battle.hero3!! },
    HERO4 { override fun getBattleHero(battle: Battle): BattleHero = battle.hero4!! },
    OPP1 { override fun getBattleHero(battle: Battle): BattleHero = battle.oppHero1!! },
    OPP2 { override fun getBattleHero(battle: Battle): BattleHero = battle.oppHero2!! },
    OPP3 { override fun getBattleHero(battle: Battle): BattleHero = battle.oppHero3!! },
    OPP4 { override fun getBattleHero(battle: Battle): BattleHero = battle.oppHero4!! };

    open fun getBattleHero(battle: Battle): BattleHero {
        throw RuntimeException("GetBattleHero not assigned")
    }
}
