package io.pacworx.ambrosia.vehicle

import io.pacworx.ambrosia.battle.Battle

enum class VehicleStat {
    OFFLINE_BATTLE_SPEED,
    BATTLE_XP,
    BATTLE_ASC_POINTS,
    BATTLE_RESSOURCE_LOOT,
    ARMOR {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                val bonus = (it.heroArmor * value) / 100
                it.heroArmor += bonus
                it.currentArmor += bonus
            }
        }
    },
    DEXTERITY {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroDexterity += value
            }
        }
    },
    DODGE {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroDodgeChance += value
            }
        }
    },
    INITIATIVE {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroInitiative += value
                it.currentSpeedBar += value
            }
        }
    },
    REFLECTION {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroReflect += value
            }
        }
    },
    RESISTANCE {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroResistance += value
            }
        }
    },
    STRENGTH {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroStrength += (it.heroStrength) * value / 100
            }
        }
    },
    CRIT_CHANCE {
        override fun apply(battle: Battle, value: Int) {
            battle.allPlayerHeroes().forEach {
                it.heroCrit += value
            }
        }
    },
    STAGE_HEAL,
    STAGE_ARMOR;

    open fun apply(battle: Battle, value: Int) {}
}
