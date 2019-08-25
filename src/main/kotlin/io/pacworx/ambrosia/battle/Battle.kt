package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import javax.persistence.*

@Entity
data class Battle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val type: BattleType,
    @Enumerated(EnumType.STRING)
    var status: BattleStatus = BattleStatus.INIT,
    val playerId: Long,
    val opponentId: Long?,
    @field:CreatedDate
    val started: Instant = Instant.now(),
    @field:LastModifiedDate
    var lastAction: Instant = Instant.now(),
    @Enumerated(EnumType.STRING)
    var active_hero: HeroPosition = HeroPosition.NONE,
    var turnsDone: Int = 0,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "hero1id")
    val hero1: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "hero2id")
    val hero2: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "hero3id")
    val hero3: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "hero4id")
    val hero4: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "opp_hero1id")
    val oppHero1: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "opp_hero2id")
    val oppHero2: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "opp_hero3id")
    val oppHero3: BattleHero?,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "opp_hero4id")
    val oppHero4: BattleHero?,
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "battle_id")
    @OrderBy("turn asc")
    val steps: MutableList<BattleStep> = mutableListOf()
) {

}
