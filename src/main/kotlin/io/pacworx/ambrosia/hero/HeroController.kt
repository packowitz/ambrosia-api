package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.exceptions.UnauthorizedException
import io.pacworx.ambrosia.hero.skills.SkillActiveTrigger
import io.pacworx.ambrosia.player.AuditLogService
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("hero")
class HeroController(private val heroService: HeroService,
                     private val heroRepository: HeroRepository,
                     private val auditLogService: AuditLogService) {

    @GetMapping("")
    fun getHeroes(@ModelAttribute("player") player: Player): List<HeroDto> {
        return heroRepository.findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(player.id).map { heroService.asHeroDto(it) }
    }

    @PostMapping("{heroId}/skill_up/{skillNumber}")
    @Transactional
    fun skillUp(@ModelAttribute("player") player: Player, @PathVariable heroId: Long, @PathVariable skillNumber: Int): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
        }
        hero.heroBase.skills.find { it.number == skillNumber }?.let { skill ->
            if (skill.skillActiveTrigger == SkillActiveTrigger.NPC_ONLY && !player.serviceAccount) {
                throw UnauthorizedException(player, "You are not allowed to skill up a NPC-Skill")
            }
            hero.skillLevelUp(player, skill.number)
        }
        auditLogService.log(player, "Skilled up S$skillNumber of hero ${hero.heroBase.name} #${hero.id}")
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/reset_skills")
    @Transactional
    fun resetSkills(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id) {
            throw UnauthorizedException(player, "You can only modify a hero you own")
        }
        hero.resetSkills()
        auditLogService.log(player, "Reset skills of hero ${hero.heroBase.name} #${hero.id} having ${hero.skillPoints} skill points")
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
