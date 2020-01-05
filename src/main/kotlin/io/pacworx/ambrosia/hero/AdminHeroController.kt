package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Hero
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroBaseRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PropertyService
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/hero")
class AdminHeroController(val heroRepository: HeroRepository,
                          val heroBaseRepository: HeroBaseRepository,
                          val heroService: HeroService,
                          val propertyService: PropertyService) {

    @PostMapping("recruit/{heroId}")
    fun recruit(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroBaseRepository.getOne(heroId).let { Hero(player.id, it) }
        heroRepository.save(hero)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/gain_level")
    fun gainLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id || !player.admin) {
            throw RuntimeException("not allowed")
        }
        if (hero.level < 60) {
            hero.level ++
            val stars = 1 + ((hero.level - 1) / 10)
            if (stars >= hero.heroBase.rarity.stars) {
                hero.stars = stars
            }
            hero.maxXp = propertyService.getHeroMaxXp(hero.level)
            heroRepository.save(hero)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/loose_level")
    fun looseLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id || !player.admin) {
            throw RuntimeException("not allowed")
        }
        if (hero.level > 1) {
            hero.level --
            val stars = 1 + ((hero.level - 1) / 10)
            if (stars >= hero.heroBase.rarity.stars) {
                hero.stars = stars
            }
            hero.maxXp = propertyService.getHeroMaxXp(hero.level)
            heroRepository.save(hero)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/skill_up/{skillNumber}")
    fun skillUp(@ModelAttribute("player") player: Player, @PathVariable heroId: Long, @PathVariable skillNumber: Int): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id || !player.admin) {
            throw RuntimeException("not allowed")
        }
        if (hero.heroBase.skills.any { it.number == skillNumber }) {
            hero.skillLevelUp(skillNumber)
            heroRepository.save(hero)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/skill_down/{skillNumber}")
    fun skillDown(@ModelAttribute("player") player: Player, @PathVariable heroId: Long, @PathVariable skillNumber: Int): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id || !player.admin) {
            throw RuntimeException("not allowed")
        }
        if (hero.heroBase.skills.any { it.number == skillNumber }) {
            hero.skillLevelDown(skillNumber)
            heroRepository.save(hero)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
