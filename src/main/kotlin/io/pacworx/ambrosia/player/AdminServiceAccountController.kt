package io.pacworx.ambrosia.player

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.hero.HeroDto
import io.pacworx.ambrosia.hero.HeroRepository
import io.pacworx.ambrosia.hero.HeroService
import io.pacworx.ambrosia.config.JwtService
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("admin/service_account")
class AdminServiceAccountController(val playerRepository: PlayerRepository,
                                    val playerService: PlayerService,
                                    val heroRepository: HeroRepository,
                                    val heroService: HeroService,
                                    val jwtService: JwtService) {

    @GetMapping("")
    fun getServiceAccounts(): List<Player> = playerRepository.findByServiceAccountIsTrueOrderByName()

    @GetMapping("{id}/heroes")
    fun getServiceAccountHeroes(@PathVariable id: Long): List<HeroDto> {
        val serviceAccount = playerRepository.findByServiceAccountIsTrueAndId(id)
        return heroRepository.findAllByPlayerIdOrderByStarsDescLevelDescHeroBase_IdAscIdAsc(serviceAccount.id)
            .map { heroService.asHeroDto(it) }
    }

    @PostMapping("new/{name}")
    @Transactional
    fun createServiceAccount(@PathVariable name: String): Player {
        return playerService.signup(name, "service@pacworx.io", "xxx", true)
    }

    @PostMapping("use/{id}")
    @Transactional
    fun useServiceAccount(@PathVariable id: Long): PlayerActionResponse {
        val serviceAccount = playerRepository.findByServiceAccountIsTrueAndId(id)
        return playerService.response(serviceAccount, jwtService.generateToken(serviceAccount))
    }
}
