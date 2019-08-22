package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import javax.persistence.*

@Entity
data class Battle(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Enumerated(EnumType.STRING)
        val type: BattleType,
        val playerId: Long,
        val opponentId: Long?,
        @OneToOne
        @JoinColumn(name = "hero1id")
        val hero1: BattleHero?,
        @OneToOne
        @JoinColumn(name = "hero2id")
        val hero2: BattleHero?,
        @OneToOne
        @JoinColumn(name = "hero3id")
        val hero3: BattleHero?,
        @OneToOne
        @JoinColumn(name = "hero4id")
        val hero4: BattleHero?,
        @OneToOne
        @JoinColumn(name = "opp_hero1id")
        val oppHero1: BattleHero?,
        @OneToOne
        @JoinColumn(name = "opp_hero2id")
        val oppHero2: BattleHero?,
        @OneToOne
        @JoinColumn(name = "opp_hero3id")
        val oppHero3: BattleHero?,
        @OneToOne
        @JoinColumn(name = "opp_hero4id")
        val oppHero4: BattleHero?
)
