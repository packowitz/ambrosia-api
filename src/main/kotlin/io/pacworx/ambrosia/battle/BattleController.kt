package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle")
class BattleController(private val battleService: BattleService,
                       private val battleRepository: BattleRepository) {

    @PostMapping
    fun startPvpBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartBattleRequest): Battle {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        return battleService.initBattle(player, request)
    }

    @PostMapping("{battleId}/{heroPos}/{skillNumber}/{targetPos}")
    fun takeTurn(@ModelAttribute("player") player: Player,
                 @PathVariable battleId: Long,
                 @PathVariable heroPos: HeroPosition,
                 @PathVariable skillNumber: Int,
                 @PathVariable targetPos: HeroPosition): Battle {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw RuntimeException("You don't own battle $battleId")
        }
        if (battle.activeHero != heroPos) {
            throw RuntimeException("It is not $heroPos's turn on battle $battleId")
        }
        val activeHero = battle.allHeroesAlive().find { it.position == battle.activeHero }
        val skill = activeHero?.heroBase?.skills?.find { it.number == skillNumber }
        if (activeHero == null || activeHero.getCooldown(skillNumber) > 0 || skill == null || skill.passive) {
            throw RuntimeException("Hero $heroPos cannot use skill $skillNumber on battle $battleId")
        }
        val target = battle.allHeroesAlive().find { it.position == targetPos }
                ?: throw java.lang.RuntimeException("Target $targetPos is not valid on battle $battleId")
        return battleService.takeTurn(battle, activeHero, skill, target)
    }

    @PostMapping("{battleId}/{heroPos}/auto")
    fun takeAutoTurn(@ModelAttribute("player") player: Player,
                 @PathVariable battleId: Long,
                 @PathVariable heroPos: HeroPosition): Battle {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw RuntimeException("You don't own battle $battleId")
        }
        if (battle.activeHero != heroPos) {
            throw RuntimeException("It is not $heroPos's turn on battle $battleId")
        }
        val activeHero = battle.allHeroesAlive().find { it.position == battle.activeHero }
                ?: throw RuntimeException("It is not Hero $heroPos 's turn on battle $battleId")
        return battleService.takeAutoTurn(battle, activeHero)
    }

    @PostMapping("{battleId}/surrender")
    @Transactional
    fun surrender(@ModelAttribute("player") player: Player,
                  @PathVariable battleId: Long): Battle {
        val battle = battleRepository.getOne(battleId)
        if (battle.playerId != player.id) {
            throw RuntimeException("You don't own battle $battleId")
        }
        battle.status = BattleStatus.LOST
        return battle
    }
}

data class StartBattleRequest(
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?,
    val oppPlayerId: Long,
    val oppHero1Id: Long?,
    val oppHero2Id: Long?,
    val oppHero3Id: Long?,
    val oppHero4Id: Long?
)
