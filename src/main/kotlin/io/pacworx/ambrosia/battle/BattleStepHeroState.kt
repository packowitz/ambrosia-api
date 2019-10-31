package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import javax.persistence.*

@Entity
data class BattleStepHeroState(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Enumerated(EnumType.STRING)
        val position: HeroPosition,
        val hpPerc: Int,
        val armorPerc: Int,
        val speedbarPerc: Int,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "battle_step_hero_state_id")
        var buffs: List<BattleStepHeroStateBuff> = listOf()
)
