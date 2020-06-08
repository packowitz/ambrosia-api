package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero")
class AdminHeroController(private val heroBaseRepository: HeroBaseRepository,
                          private val heroService: HeroService,
                          private val auditLogService: AuditLogService) {

    @PostMapping("recruit/{heroId}")
    fun recruit(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroService.recruitHero(player, heroBaseRepository.getOne(heroId))
        auditLogService.log(player, "Direct recruits hero ${hero.heroBase.name} ${hero.id}", adminAction = true)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
