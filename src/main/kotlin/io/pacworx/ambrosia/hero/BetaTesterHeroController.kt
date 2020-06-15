package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.hero.skills.SkillActiveTrigger
import io.pacworx.ambrosia.player.AuditLogService
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
class BetaTesterHeroController(private val heroRepository: HeroRepository,
                               private val heroService: HeroService,
                               private val propertyService: PropertyService,
                               private val auditLogService: AuditLogService) {

    @PostMapping("{heroId}/gain_level")
    @Transactional
    fun gainLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
        }
        if (!heroService.evolveHero(hero)) {
            heroService.heroGainXp(hero, hero.maxXp - hero.xp)
        }
        auditLogService.log(player, "Increased level of hero ${hero.heroBase.name} #${hero.id} to ${hero.level}", betaTesterAction = true)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/loose_level")
    @Transactional
    fun looseLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
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
        auditLogService.log(player, "Decreased level of hero ${hero.heroBase.name} #${hero.id} to ${hero.level}", betaTesterAction = true)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/gain_asc_level")
    @Transactional
    fun gainAscLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
        }
        heroService.heroGainAsc(hero, hero.ascPointsMax - hero.ascPoints)
        auditLogService.log(player, "Increased asc level of hero ${hero.heroBase.name} #${hero.id} to ${hero.ascLvl}", betaTesterAction = true)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/loose_asc_level")
    @Transactional
    fun looseAscLevel(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
        }
        if (hero.ascLvl > 0 && hero.skillPoints > 0) {
            hero.skillPoints --
            hero.ascLvl --
            hero.ascPoints = 0
            hero.ascPointsMax = propertyService.getHeroMaxAsc(hero.ascLvl)
            if (hero.ascLvl == 0) {
                hero.heroBase.skills
                    .filter { it.skillActiveTrigger == SkillActiveTrigger.ASCENDED }
                    .forEach { skill ->
                        hero.disableSkill(skill.number)
                    }
            }
        }
        auditLogService.log(player, "Decreased asc level of hero ${hero.heroBase.name} #${hero.id} to ${hero.ascLvl}", betaTesterAction = true)
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
