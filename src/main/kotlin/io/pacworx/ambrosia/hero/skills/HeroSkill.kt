package io.pacworx.ambrosia.hero.skills

import javax.persistence.*
import kotlin.math.max

@Entity
data class HeroSkill(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val number: Int,
    var name: String,
    var icon: String,
    var passive: Boolean,
    @Enumerated(EnumType.STRING)
    var passiveSkillTrigger: PassiveSkillTrigger? = null,
    var passiveSkillTriggerValue: Int? = null,
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

    fun getCooldown(skillLevel: Int?): Int {
        if (skillLevel == null || skillLevel <= 0) {
            return 999
        }
        var cooldown = this.cooldown
        triggeredActionsOfEffect(SkillActionEffect.COOLDOWN, skillLevel).forEach { cooldown += it.effectValue }
        return max(0, cooldown)
    }

    fun getInitCooldown(skillLevel: Int?): Int {
        if (skillLevel == null || skillLevel <= 0) {
            return 999
        }
        var initCooldown = this.initCooldown
        triggeredActionsOfEffect(SkillActionEffect.INIT_COOLDOWN, skillLevel).forEach { initCooldown += it.effectValue }
        return max(0, initCooldown)
    }

    private fun triggeredActionsOfEffect(effect: SkillActionEffect, skillLevel: Int): List<HeroSkillAction> {
        return this.actions
            .filter {
                it.effect == effect && when (it.trigger) {
                    SkillActionTrigger.ALWAYS -> true
                    SkillActionTrigger.S1_LVL -> this.number == 1 && it.triggerValue!!.contains(skillLevel.toString())
                    SkillActionTrigger.S2_LVL -> this.number == 2 && it.triggerValue!!.contains(skillLevel.toString())
                    SkillActionTrigger.S3_LVL -> this.number == 3 && it.triggerValue!!.contains(skillLevel.toString())
                    SkillActionTrigger.S4_LVL -> this.number == 4 && it.triggerValue!!.contains(skillLevel.toString())
                    SkillActionTrigger.S5_LVL -> this.number == 5 && it.triggerValue!!.contains(skillLevel.toString())
                    SkillActionTrigger.S6_LVL -> this.number == 6 && it.triggerValue!!.contains(skillLevel.toString())
                    SkillActionTrigger.S7_LVL -> this.number == 7 && it.triggerValue!!.contains(skillLevel.toString())
                    else -> false
                }
            }
    }
}
