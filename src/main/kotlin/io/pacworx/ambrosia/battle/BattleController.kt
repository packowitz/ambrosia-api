package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.SkillTarget
import io.pacworx.ambrosia.io.pacworx.ambrosia.maps.SimplePlayerMapTileRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkill
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle")
class BattleController(private val battleService: BattleService,
                       private val battleRepository: BattleRepository,
                       private val simplePlayerMapTileRepository: SimplePlayerMapTileRepository) {

    @GetMapping("{battleId}")
    fun getBattle(@PathVariable battleId: Long): Battle {
        val battle = battleRepository.getOne(battleId)
        return if (battle.status == BattleStatus.INIT) {
            battleService.startBattle(battle)
        } else {
            battle
        }
    }

    @PostMapping
    fun startPvpBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartDuellRequest): Battle {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        return battleService.initDuell(player, request)
    }

    @PostMapping("dungeon/{dungeonId}")
    fun startDungeon(@ModelAttribute("player") player: Player,
                     @PathVariable dungeonId: Long,
                     @RequestBody request: StartBattleRequest): Battle {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        return battleService.initDungeon(player, dungeonId, request)
    }

    @PostMapping("campaign/{mapId}/{posX}/{posY}")
    fun startCampaign(@ModelAttribute("player") player: Player,
                     @PathVariable mapId: Long,
                     @PathVariable posX: Int,
                     @PathVariable posY: Int,
                     @RequestBody request: StartBattleRequest): Battle {
        if (battleRepository.findTopByPlayerIdAndStatusNotIn(player.id, listOf(BattleStatus.LOST, BattleStatus.WON)) != null) {
            throw RuntimeException("Finish your ongoing battle before starting a new one")
        }
        val mapTile = simplePlayerMapTileRepository.findPlayerMapTile(player.id, mapId, posX, posY)
        if (mapTile == null || !mapTile.discovered || mapTile.dungeonId == null || (mapTile.victoriousFight && !mapTile.fightRepeatable)) {
            throw RuntimeException("You cannot fight on that map tile.")
        }
        return battleService.initDungeon(player, mapTile.dungeonId, request)
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
        val target = battle.allHeroes().find { it.position == targetPos }
                ?: throw RuntimeException("Target $targetPos is not valid on battle $battleId")
        if (!isTargetEligible(battle, activeHero, skill, target)) {
            throw RuntimeException("Target ${target.position} is not valid target for skill ${skill.number} of hero ${activeHero.position} in battle ${battle.id}")
        }
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

    private fun isTargetEligible(battle: Battle, hero: BattleHero, skill: HeroSkill, target: BattleHero): Boolean {
        val isPlayer = battle.heroBelongsToPlayer(hero)
        return when (skill.target) {
            SkillTarget.OPPONENT -> isPlayer == battle.heroBelongsToOpponent(target) && (target.isTaunting() || battle.allAlliedHeroesAlive(target).none { it.isTaunting() })
            SkillTarget.SELF -> hero.position == target.position
            SkillTarget.ALL_OWN -> isPlayer == battle.heroBelongsToPlayer(target)
            SkillTarget.OPP_IGNORE_TAUNT -> isPlayer == battle.heroBelongsToOpponent(target)
            SkillTarget.DEAD -> isPlayer == battle.heroBelongsToPlayer(target) && target.status == HeroStatus.DEAD
        }
    }
}

data class StartDuellRequest(
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

data class StartBattleRequest(
    val hero1Id: Long?,
    val hero2Id: Long?,
    val hero3Id: Long?,
    val hero4Id: Long?
)
