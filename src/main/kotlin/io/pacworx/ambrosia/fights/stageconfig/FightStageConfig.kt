package io.pacworx.ambrosia.io.pacworx.ambrosia.fights.stageconfig

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class FightStageConfig(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val defaultConfig: Boolean,
    val debuffsRemoved: Boolean,
    val debuffDurationChange: Int,
    val buffsRemoved: Boolean,
    val buffDurationChange: Int,
    val hpHealing: Int,
    val armorRepair: Int,
    @Enumerated(EnumType.STRING) val speedBarChange: SpeedBarChange
)
