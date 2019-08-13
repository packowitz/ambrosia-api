package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionEffect
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionTarget
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionTrigger
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActionType
import javax.persistence.*

@Entity
data class HeroSkillAction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var position: Int,
    @Enumerated(EnumType.STRING)
    var trigger: SkillActionTrigger,
    var triggerValue: String?,
    var triggerChance: Int,
    @Enumerated(EnumType.STRING)
    var type: SkillActionType,
    @Enumerated(EnumType.STRING)
    var target: SkillActionTarget,
    @Enumerated(EnumType.STRING)
    var effect: SkillActionEffect,
    var effectValue: Double,
    var efectDuration: Int
)
