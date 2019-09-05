package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import javax.persistence.*

@Entity
data class BattleStep(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val turn: Int,
    @Enumerated(EnumType.STRING)
    val actingHero: HeroPosition,
    val usedSkill: Int,
    @Enumerated(EnumType.STRING)
    val target: HeroPosition,
    @OneToMany
    @JoinColumn(name = "battle_step_id")
    val actions: MutableList<BattleStepAction> = mutableListOf()
) {
    fun addAction(action: BattleStepAction) {
        actions.add(action)
    }
}
