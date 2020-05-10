package io.pacworx.ambrosia.common

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
