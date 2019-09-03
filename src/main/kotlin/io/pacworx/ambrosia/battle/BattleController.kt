package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("battle")
class BattleController(private val battleService: BattleService) {

    @PostMapping
    fun startPvpBattle(@ModelAttribute("player") player: Player, @RequestBody request: StartBattleRequest): Battle {
        return battleService.initBattle(player, request)
    }

    @PostMapping("{battleId}/")
    fun useSkill(@ModelAttribute("player") player: Player) {

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
