package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import com.fasterxml.jackson.annotation.JsonInclude
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Gear
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Jewelry
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlayerActionResponse(
    val player: Player? = null,
    val token: String? = null,
    val hero: HeroDto? = null,
    val heroes: List<HeroDto>? = null,
    val heroIdsRemoved: List<Long>? = null,
    val gear: Gear? = null,
    val gears: List<Gear>? = null,
    val gearIdsRemovedFromArmory: List<Long>? = null,
    val jewelries: List<Jewelry>? = null
)
