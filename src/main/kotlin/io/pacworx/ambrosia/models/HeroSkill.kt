package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillActiveTrigger
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillTarget
import javax.persistence.*

@Entity
data class HeroSkill(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val number: Int,
    var name: String,
    var passive: Boolean,
    @Enumerated(EnumType.STRING)
    var skillActiveTrigger: SkillActiveTrigger,
    var initCooldown: Int,
    var cooldown: Int,
    @Enumerated(EnumType.STRING)
    var target: SkillTarget,
    var description: String,
    var maxLevel: Int
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "skill_id")
    @OrderBy("level ASC")
    var skillLevel: List<HeroSkillLevel> = ArrayList()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "skill_id")
    @OrderBy("position")
    var actions: List<HeroSkillAction> = ArrayList()
}