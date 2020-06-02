package io.pacworx.ambrosia.battle

import com.fasterxml.jackson.annotation.JsonFormat
import javax.persistence.*

@Entity
data class BattleStepAction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val heroPosition: HeroPosition,
    val heroName: String,
    @Enumerated(EnumType.STRING)
    val type: BattleStepActionType,
    val crit: Boolean? = null,
    val superCrit: Boolean? = null,
    val baseDamage: Int? = null,
    val baseDamageText: String? = null,
    val targetArmor: Int? = null,
    val targetHealth: Int? = null,
    val armorDiff: Int? = null,
    val healthDiff: Int? = null,
    val shieldAbsorb: Int? = null,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val buff: Buff? = null,
    val buffIntensity: Int? = null,
    val buffDuration: Int? = null,
    val buffDurationChange: Int? = null,
    val speedbarFill: Int? = null
)
