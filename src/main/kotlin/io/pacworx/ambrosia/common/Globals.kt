package io.pacworx.ambrosia.common

import io.pacworx.ambrosia.hero.Rarity
import kotlin.random.Random

fun procs(chance: Int, random: Int = Random.nextInt(100)): Boolean {
    if (chance >= 100) {
        return true
    }
    if (chance <= 0) {
        return false
    }
    return random < chance
}

fun randomRarity(): Rarity {
    val random = Random.nextInt(100)
    if (procs(1, random)) { return Rarity.LEGENDARY }
    if (procs(3, random)) { return Rarity.EPIC }
    if (procs(6, random)) { return Rarity.RARE }
    if (procs(15, random)) { return Rarity.UNCOMMON }
    if (procs(25, random)) { return Rarity.COMMON }
    return Rarity.SIMPLE
}
