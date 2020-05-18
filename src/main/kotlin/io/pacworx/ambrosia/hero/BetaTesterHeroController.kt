package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.properties.PropertyService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("tester/hero")
class BetaTesterHeroController(val heroRepository: HeroRepository,
                               val heroService: HeroService,
                               val propertyService: PropertyService) {

    @PostMapping("{heroId}/gain_level")
    @Transactional
    fun gainLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw RuntimeException("not allowed")
        }
        if (!heroService.evolveHero(hero)) {
            heroService.heroGainXp(hero, hero.maxXp - hero.xp)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/loose_level")
    @Transactional
    fun looseLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw RuntimeException("not allowed")
        }
        if (hero.level > 1) {
            hero.level --
            val stars = 1 + ((hero.level - 1) / 10)
            if (stars > hero.heroBase.rarity.stars) {
                hero.stars = stars
            } else {
                hero.stars = hero.heroBase.rarity.stars
            }
            hero.maxXp = propertyService.getHeroMaxXp(hero.level)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/gain_asc_level")
    @Transactional
    fun gainAscLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw RuntimeException("not allowed")
        }
        heroService.heroGainAsc(hero, hero.ascPointsMax - hero.ascPoints)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/loose_asc_level")
    @Transactional
    fun looseAscLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw RuntimeException("not allowed")
        }
        if (hero.ascLvl > 0 && hero.skillPoints > 0) {
            hero.skillPoints --
            hero.ascLvl --
            hero.ascPoints = 0
            hero.ascPointsMax = propertyService.getHeroMaxAsc(hero.ascLvl)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}