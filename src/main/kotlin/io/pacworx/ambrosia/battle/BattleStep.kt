package io.pacworx.ambrosia.battle

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class BattleStep(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val battleId: Long,
    val turn: Int,
    @Enumerated(EnumType.STRING)
    val phase: BattleStepPhase,
    @Enumerated(EnumType.STRING)
    val actingHero: HeroPosition,
    val actingHeroName: String,
    val usedSkill: Int? = null,
    val usedSkillName: String? = null,
    @Enumerated(EnumType.STRING)
    val target: HeroPosition,
    val targetName: String,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "battle_step_id")
    val actions: MutableList<BattleStepAction> = mutableListOf(),
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "battle_step_id")
    val heroStates: List<BattleStepHeroState>
) {

    @Transient
    @JsonIgnore
    var baseDamageText: String? = null

    fun addAction(action: BattleStepAction) {
        actions.add(action)
    }

    fun addBaseDamageText(text: String) {
        if (baseDamageText.isNullOrBlank()) {
            baseDamageText = text
        } else {
            baseDamageText += " $text"
        }
    }
}
