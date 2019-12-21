package io.pacworx.ambrosia.io.pacworx.ambrosia.admin.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroDto
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.PlayerRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.HeroService
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.JwtService
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PlayerService
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

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
        val serviceAccount = Player(
                name = name,
                serviceAccount = true,
                admin = true,
                email = "service@pacworx.io",
                password = "xxx")
        return playerRepository.save(serviceAccount)
    }

    @PostMapping("use/{id}")
    fun useServiceAccount(@PathVariable id: Long): PlayerActionResponse {
        val serviceAccount = playerRepository.findByServiceAccountIsTrueAndId(id)
        return playerService.response(serviceAccount, jwtService.generateToken(serviceAccount))
    }
}
