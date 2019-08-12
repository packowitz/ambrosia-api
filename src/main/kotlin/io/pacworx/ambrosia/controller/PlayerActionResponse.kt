package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Gear
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Jewelry
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player

data class PlayerActionResponse(
        val player: Player? = null,
        val hero: Hero? = null,
        val heroIdsRemoved: List<Long>? = null,
        val gear: Gear? = null,
        val gearIdsRemovedFromArmory: List<Long>? = null,
        val jewelries: List<Jewelry>? = null
)
