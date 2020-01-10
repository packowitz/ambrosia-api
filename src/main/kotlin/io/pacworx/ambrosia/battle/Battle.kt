package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.fights.Fight
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import javax.persistence.*
import kotlin.math.max
import kotlin.math.min

@Entity
data class Battle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    val type: BattleType,
    @Enumerated(EnumType.STRING)
    var status: BattleStatus = BattleStatus.INIT,
    var previousBattleId: Long? = null,
    var nextBattleId: Long? = null,
    @ManyToOne
    @JoinColumn(name = "fight_id")
    var fight: Fight? = null,
    var fightStage: Int? = null,
    val mapId: Long? = null,
    @Column(name = "map_pos_x") val mapPosX: Int? = null,
    @Column(name = "map_pos_y") val mapPosY: Int? = null,
    val playerId: Long,
    val playerName: String,
    val opponentId: Long? = null,
    val opponentName: String,
    @field:CreatedDate
    val started: Instant = Instant.now(),
    @field:LastModifiedDate
    var lastAction: Instant = Instant.now(),
    @Enumerated(EnumType.STRING)
    var activeHero: HeroPosition = HeroPosition.NONE,
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
    @OrderBy("turn asc, phase asc, id asc")
    val steps: MutableList<BattleStep> = mutableListOf()
) {

    fun applyBonuses(propertyService: PropertyService) {
        hero1?.resetBonus(this, propertyService)
        hero2?.resetBonus(this, propertyService)
        hero3?.resetBonus(this, propertyService)
        hero4?.resetBonus(this, propertyService)
        oppHero1?.resetBonus(this, propertyService)
        oppHero2?.resetBonus(this, propertyService)
        oppHero3?.resetBonus(this, propertyService)
        oppHero4?.resetBonus(this, propertyService)
    }

    fun allHeroes(): List<BattleHero> {
        return listOfNotNull(hero1, hero2, hero3, hero4, oppHero1, oppHero2, oppHero3, oppHero4)
    }

    fun allHeroesAlive(): List<BattleHero> {
        return allHeroes().filter { it.status != HeroStatus.DEAD }
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
        return if (heroBelongsToPlayer(hero)) {
            allPlayerHeroesAlive()
        } else {
            allOppHeroesAlive()
        }
    }

    fun allAlliedHeroesDead(hero: BattleHero): List<BattleHero> {
        return if (heroBelongsToPlayer(hero)) {
            allPlayerHeroesDead()
        } else {
            allOppHeroesDead()
        }
    }

    fun allOtherHeroesAlive(hero: BattleHero): List<BattleHero> {
        return if (heroBelongsToPlayer(hero)) {
            allOppHeroesAlive()
        } else {
            allPlayerHeroesAlive()
        }
    }

    fun setActiveHero(hero: BattleHero) {
        lastAction = Instant.now()
        turnsDone ++
        hero1?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.HERO1
            status = BattleStatus.PLAYER_TURN
        }
        hero2?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.HERO2
            status = BattleStatus.PLAYER_TURN
        }
        hero3?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.HERO3
            status = BattleStatus.PLAYER_TURN
        }
        hero4?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.HERO4
            status = BattleStatus.PLAYER_TURN
        }
        oppHero1?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.OPP1
            status = BattleStatus.OPP_TURN
        }
        oppHero2?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.OPP2
            status = BattleStatus.OPP_TURN
        }
        oppHero3?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.OPP3
            status = BattleStatus.OPP_TURN
        }
        oppHero4?.takeIf { it.id == hero.id }?.let {
            activeHero = HeroPosition.OPP4
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
            status = fight?.stages?.find { it.stage > fightStage!! }?.let { BattleStatus.STAGE_PASSED } ?: BattleStatus.WON
        }
    }

    fun resolveHeroName(position: HeroPosition): String {
        return when(position) {
            HeroPosition.HERO1 -> hero1
            HeroPosition.HERO2 -> hero2
            HeroPosition.HERO3 -> hero3
            HeroPosition.HERO4 -> hero4
            HeroPosition.OPP1 -> oppHero1
            HeroPosition.OPP2 -> oppHero2
            HeroPosition.OPP3 -> oppHero3
            HeroPosition.OPP4 -> oppHero4
            else -> null
        }?.heroBase?.name ?: "unknown"
    }

    @JsonIgnore
    fun getPreTurnStep(): BattleStep {
        return steps.find { it.turn == this.turnsDone && it.phase == BattleStepPhase.A_PRE_TURN }
                ?: run {
                    val step = BattleStep(
                        turn = this.turnsDone,
                        phase = BattleStepPhase.A_PRE_TURN,
                        actingHero = this.activeHero,
                        actingHeroName = resolveHeroName(this.activeHero),
                        target = this.activeHero,
                        targetName = resolveHeroName(this.activeHero),
                        heroStates = getBattleStepHeroStates()
                    )
                    steps.add(step)
                    step
                }
    }

    @JsonIgnore
    fun getBattleStepHeroStates(): List<BattleStepHeroState> {
        return allHeroes().map { hero ->
            BattleStepHeroState(
                    position = hero.position,
                    status = hero.status,
                    hpPerc = max(min((100 * hero.currentHp) / hero.heroHp, 100), 0),
                    armorPerc = max(min((100 * hero.currentArmor) / hero.heroArmor, 100), 0),
                    speedbarPerc = min(hero.currentSpeedBar / 100, 100),
                    buffs = hero.buffs.map { buff ->
                        BattleStepHeroStateBuff(
                                buff = buff.buff,
                                intensity = buff.intensity,
                                duration = buff.duration
                        )
                    }
            )
        }
    }
}
