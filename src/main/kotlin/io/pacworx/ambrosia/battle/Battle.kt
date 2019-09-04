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

    fun allHeroesAlive(): List<BattleHero> {
        return listOfNotNull(hero1, hero2, hero3, hero4, oppHero1, oppHero2, oppHero3, oppHero4)
            .filter { it.status != HeroStatus.DEAD }
    }

    fun allPlayerHeroesAlive(): List<BattleHero> {
        return listOfNotNull(hero1, hero2, hero3, hero4).filter { it.status != HeroStatus.DEAD }
    }

    fun allPlayerHeroesDead(): List<BattleHero> {
        return listOfNotNull(hero1, hero2, hero3, hero4).filter { it.status == HeroStatus.DEAD }
    }

    fun allOppHeroesAlive(): List<BattleHero> {
        return listOfNotNull(oppHero1, oppHero2, oppHero3, oppHero4).filter { it.status != HeroStatus.DEAD }
    }

    fun allOppHeroesDead(): List<BattleHero> {
        return listOfNotNull(oppHero1, oppHero2, oppHero3, oppHero4).filter { it.status == HeroStatus.DEAD }
    }

    fun allAlliedHeroesAlive(hero: BattleHero): List<BattleHero> {
        return if (hero.playerId == playerId) {
            allPlayerHeroesAlive()
        } else {
            allOppHeroesAlive()
        }
    }

    fun allAlliedHeroesDead(hero: BattleHero): List<BattleHero> {
        return if (hero.playerId == playerId) {
            allPlayerHeroesDead()
        } else {
            allOppHeroesDead()
        }
    }

    fun allOtherHeroesAlive(hero: BattleHero): List<BattleHero> {
        return if (hero.playerId == playerId) {
            allOppHeroesAlive()
        } else {
            allPlayerHeroesAlive()
        }
    }

    fun setActiveHero(hero: BattleHero) {
        hero1?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.HERO1
            status = BattleStatus.PLAYER_TURN
        }
        hero2?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.HERO2
            status = BattleStatus.PLAYER_TURN
        }
        hero3?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.HERO3
            status = BattleStatus.PLAYER_TURN
        }
        hero4?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.HERO4
            status = BattleStatus.PLAYER_TURN
        }
        oppHero1?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.OPP1
            status = BattleStatus.OPP_TURN
        }
        oppHero2?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.OPP2
            status = BattleStatus.OPP_TURN
        }
        oppHero3?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.OPP3
            status = BattleStatus.OPP_TURN
        }
        oppHero4?.takeIf { it.id == hero.id }?.let {
            active_hero = HeroPosition.OPP4
            status = BattleStatus.OPP_TURN
        }
    }

    fun heroBelongsToPlayer(hero: BattleHero): Boolean {
        return hero.id == hero1?.id || hero.id == hero2?.id || hero.id == hero3?.id || hero.id == hero4?.id
    }

    fun heroBelongsToOpponent(hero: BattleHero): Boolean {
        return hero.id == oppHero1?.id || hero.id == oppHero2?.id || hero.id == oppHero3?.id || hero.id == oppHero4?.id
    }

    fun checkStatus() {
        if (allPlayerHeroesAlive().isEmpty()) {
            status = BattleStatus.LOST
        } else if (allOppHeroesAlive().isEmpty()) {
            status = BattleStatus.WON
        }
    }
}
