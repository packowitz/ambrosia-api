package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.player.Player
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("hero")
class HeroController(val heroService: HeroService,
                     val heroRepository: HeroRepository) {

    @GetMapping("")
    fun getHeroes(@ModelAttribute("player") player: Player): List<HeroDto> {
        return heroRepository.findAllByPlayerIdOrderByLevelDescStarsDescHeroBase_IdAscIdAsc(player.id).map { heroService.asHeroDto(it) }
    }

    @PostMapping("{heroId}/skill_up/{skillNumber}")
    @Transactional
    fun skillUp(@ModelAttribute("player") player: Player, @PathVariable heroId: Long, @PathVariable skillNumber: Int): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id || !player.admin) {
            throw RuntimeException("not allowed")
        }
        if (hero.heroBase.skills.any { it.number == skillNumber }) {
            hero.skillLevelUp(skillNumber)
        }
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

    @PostMapping("{heroId}/reset_skills")
    @Transactional
    fun resetSkills(@ModelAttribute("player") player: Player, @PathVariable heroId: Long): PlayerActionResponse {
        val hero = heroRepository.getOne(heroId)
        if (hero.playerId != player.id || !player.admin) {
            throw RuntimeException("not allowed")
        }
        hero.resetSkills()
        return PlayerActionResponse(hero = heroService.asHeroDto(hero))
    }

}
