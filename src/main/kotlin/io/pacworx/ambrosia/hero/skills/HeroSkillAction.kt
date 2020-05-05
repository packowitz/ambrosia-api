package io.pacworx.ambrosia.hero.skills

import com.fasterxml.jackson.annotation.JsonFormat
import javax.persistence.*

@Entity
data class HeroSkillAction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var position: Int,
    @Enumerated(EnumType.STRING)
    var trigger: SkillActionTrigger,
    var triggerValue: String? = null,
    var triggerChance: Int,
    @Enumerated(EnumType.STRING)
    var type: SkillActionType,
    @Enumerated(EnumType.STRING)
    var target: SkillActionTarget,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var effect: SkillActionEffect,
    var effectValue: Int,
    var effectDuration: Int? = null
)
