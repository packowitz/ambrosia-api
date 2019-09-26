package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.GearSet

data class HeroGearSet(
        val gearSet: GearSet,
        var number: Int = 0,
        var description: String = ""
)
