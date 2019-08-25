package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Buff
import javax.persistence.*

@Entity
data class BattleStepAction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val heroPosition: HeroPosition,
    val crit: Boolean? = null,
    val superCrit: Boolean? = null,
    val armorDiff: Int? = null,
    val healthDiff: Int? = null,
    @Enumerated(EnumType.STRING)
    val buff: Buff?,
    val buffIntensity: Int?,
    val buffDuration: Int?,
    val buffDurationChange: Int?
)