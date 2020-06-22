package io.pacworx.ambrosia.player

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.hero.Color
import io.pacworx.ambrosia.hero.HeroService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("player")
class PlayerController(private val playerService: PlayerService,
                       private val heroService: HeroService) {

    @PostMapping("")
    @Transactional
    fun getPlayer(@ModelAttribute("player") player: Player): PlayerActionResponse {
        return playerService.response(player)
    }

    @PostMapping("color")
    @Transactional
    fun choseColor(@ModelAttribute("player") player: Player, @RequestBody request: ColorRequest): PlayerActionResponse {
        if (player.color != null) {
            throw RuntimeException("You already have a color selected")
        }
        if (!listOf(Color.BLUE, Color.RED, Color.GREEN).contains(request.color)) {
           throw RuntimeException("You can only select RED, BLUE or GREEN")
        }
        player.color = request.color
        val startingHeroes = heroService.gainStartingHeroes(player)
        return PlayerActionResponse(
            player = playerService.save(player),
            heroes = startingHeroes
        )
    }
}

data class ColorRequest(val color: Color)
