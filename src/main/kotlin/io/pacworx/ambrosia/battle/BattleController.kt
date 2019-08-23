package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.Battle
import io.pacworx.ambrosia.io.pacworx.ambrosia.battle.BattleType
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle")
class BattleController(private val heroService: HeroService) {

    @PostMapping
    fun startPvpBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartBattleRequest) {
        val heroes = heroService.loadHeroes(listOfNotNull(
                request.hero1Id, request.hero2Id, request.hero3Id, request.hero4Id,
                request.oppHero1Id, request.oppHero1Id, request.oppHero1Id, request.oppHero4Id))
        Battle(
                type = BattleType.DUELL,
                playerId = player.id,
                opponentId = request.oppPlayerId,
                hero1 = request.hero1Id?.let { heroId -> heroes.find { it.id == heroId } }
        )
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
