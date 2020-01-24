package io.pacworx.ambrosia.gear

import io.pacworx.ambrosia.enums.GearSet

data class HeroGearSet(
        val gearSet: GearSet,
        var number: Int = 0,
        var description: String = ""
)
