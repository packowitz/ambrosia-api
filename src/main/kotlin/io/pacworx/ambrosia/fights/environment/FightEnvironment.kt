package io.pacworx.ambrosia.io.pacworx.ambrosia.fights.environment

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Entity
data class FightEnvironment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val defaultEnvironment: Boolean = false,
    @field:Min(0)
    @field:Max(100)
    val playerHealingDec: Int = 0,
    val playerHotBlocked: Boolean = false,
    @field:Min(0)
    val playerDotDmgInc: Int = 0,
    @field:Min(0)
    @field:Max(100)
    val oppDotDmgDec: Int = 0,
    @field:Min(0)
    val playerGreenDmgInc: Int = 0,
    @field:Min(0)
    val playerRedDmgInc: Int = 0,
    @field:Min(0)
    val playerBlueDmgInc: Int = 0,
    @field:Min(0)
    @field:Max(100)
    val oppGreenDmgDec: Int = 0,
    @field:Min(0)
    @field:Max(100)
    val oppRedDmgDec: Int = 0,
    @field:Min(0)
    @field:Max(100)
    val oppBlueDmgDec: Int = 0,
    @field:Min(0)
    @field:Max(100)
    val playerSpeedBarSlowed: Int = 0,
    @field:Min(0)
    val oppSpeedBarFastened: Int = 0
)
