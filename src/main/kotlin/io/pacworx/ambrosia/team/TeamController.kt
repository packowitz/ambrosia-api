package io.pacworx.ambrosia.team

import io.pacworx.ambrosia.enums.TeamType
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.player.PlayerRepository
import io.pacworx.ambrosia.hero.HeroService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import java.lang.RuntimeException

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("teams")
class TeamController(private val teamRepository: TeamRepository,
                     private val playerRepository: PlayerRepository,
                     private val heroRepository: HeroRepository,
                     private val heroService: HeroService) {

    @GetMapping
    fun getOwnTeams(@ModelAttribute("player") player: Player): List<Team> {
        return teamRepository.getAllByPlayerId(player.id)
    }

    @PostMapping("type/{type}")
    fun saveNewTeam(@ModelAttribute("player") player: Player, @PathVariable type: TeamType, @RequestBody request: Team): Team {
        return teamRepository.save(request.copy(id = 0, playerId = player.id, type = type))
    }

    @PutMapping("{id}")
    fun updateTeam(@ModelAttribute("player") player: Player, @PathVariable id: Long, @RequestBody request: Team): Team {
        return teamRepository.findByIdOrNull(id)?.takeIf { it.playerId == player.id }?.let {
            teamRepository.save(it.copy(hero1Id = request.hero1Id, hero2Id = request.hero2Id, hero3Id = request.hero3Id, hero4Id = request.hero4Id))
        } ?: throw RuntimeException("Cannot update team")
    }

    @GetMapping("type/{type}")
    fun getAnyTeamsByType(@PathVariable type: TeamType): List<OtherTeam> {
        return teamRepository.getTeamsByType(type.name, 10).map { team ->
            OtherTeam(
                    playerRepository.getOne(team.playerId),
                    team.hero1Id?.let { heroRepository.findByIdOrNull(it)?.let { heroService.asHeroDto(it) } },
                    team.hero2Id?.let { heroRepository.findByIdOrNull(it)?.let { heroService.asHeroDto(it) } },
                    team.hero3Id?.let { heroRepository.findByIdOrNull(it)?.let { heroService.asHeroDto(it) } },
                    team.hero4Id?.let { heroRepository.findByIdOrNull(it)?.let { heroService.asHeroDto(it) } }
            )
        }
    }
}

data class OtherTeam(val playerName: String,
                     val playerId: Long,
                     val hero1: HeroDto?,
                     val hero2: HeroDto?,
                     val hero3: HeroDto?,
                     val hero4: HeroDto?) {
    constructor(player: Player, hero1: HeroDto?, hero2: HeroDto?, hero3: HeroDto?, hero4: HeroDto?) :this(player.name, player.id, hero1, hero2, hero3, hero4)
}
