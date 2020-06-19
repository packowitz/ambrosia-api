package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.EntityNotFoundException
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.data.repository.findByIdOrNull
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
                          private val heroRepository: HeroRepository,
                          private val auditLogService: AuditLogService) {

    @PostMapping("recruit/{heroId}")
    fun recruit(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroService.recruitHero(player, heroBaseRepository.getOne(heroId))
        auditLogService.log(player, "Direct recruits hero ${hero.heroBase.name} #${hero.id}", adminAction = true)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/boss/{isBoss}")
    fun boss(@ModelAttribute("player") player: Player,
             @PathVariable heroId: Long,
             @PathVariable isBoss: Boolean): PlayerActionResponse {
        if (!player.serviceAccount) {
            throw UnauthorizedException(player, "Only service accounts are allowed to mark a hero as a boss")
        }
        val hero = heroRepository.findByIdOrNull(heroId) ?: throw EntityNotFoundException(player, "hero", heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
        }
        auditLogService.log(player, "Marking hero ${hero.heroBase.name} #${hero.id} as boss: $isBoss", adminAction = true)
        hero.markedAsBoss = isBoss
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
