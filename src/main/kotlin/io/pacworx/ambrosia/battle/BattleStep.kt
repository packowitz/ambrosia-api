package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import javax.persistence.*

@Entity
data class BattleStep(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val battleId: Long,
        val turn: Int,
        @Enumerated(EnumType.STRING)
        val heroTurn: HeroTurn,
        val usedSkill: Int,
        @Enumerated(EnumType.STRING)
        val target: HeroTurn

)
