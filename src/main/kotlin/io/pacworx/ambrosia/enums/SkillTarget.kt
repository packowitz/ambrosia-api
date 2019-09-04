package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleHero

enum class SkillTarget {
    OPPONENT {
        override fun resolve(battle: Battle, hero: BattleHero): List<BattleHero> {
            return battle.allOtherHeroesAlive(hero)
        }
    },
    SELF {
        override fun resolve(battle: Battle, hero: BattleHero): List<BattleHero> {
            return listOf(hero)
        }
    },
    ALL_OWN {
        override fun resolve(battle: Battle, hero: BattleHero): List<BattleHero> {
            return battle.allAlliedHeroesAlive(hero)
        }
    },
    OPP_IGNORE_TAUNT {
        override fun resolve(battle: Battle, hero: BattleHero): List<BattleHero> {
            return battle.allOtherHeroesAlive(hero)
        }
    },
    DEAD {
        override fun resolve(battle: Battle, hero: BattleHero): List<BattleHero> {
            return battle.allAlliedHeroesDead(hero)
        }
    };

    abstract fun resolve(battle: Battle, hero: BattleHero): List<BattleHero>
}
