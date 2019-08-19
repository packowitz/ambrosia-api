package io.pacworx.ambrosia.io.pacworx.ambrosia.services

import com.google.common.hash.Hashing
import io.pacworx.ambrosia.io.pacworx.ambrosia.controller.PlayerActionResponse
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets


@Service
class PlayerService(private val playerRepository: PlayerRepository,
                    private val heroService: HeroService,
                    private val heroRepository: HeroRepository,
                    private val gearRepository: GearRepository,
                    private val jewelryRepository: JewelryRepository) {

    @Value("\${ambrosia.pw-salt-one}")
    private lateinit var pwSalt1: String
    @Value("\${ambrosia.pw-salt-two}")
    private lateinit var pwSalt2: String

    fun signup(name: String, email: String, password: String): Player {
        val player = Player(name = name, email = email, password = getHash(name, password))
        return playerRepository.save(player)
    }

    fun login(email: String, password: String): Player {
        return playerRepository.findByEmail(email)?.takeIf { getHash(it.name, password) == it.password }
                ?: throw RuntimeException("Auth failed")
    }

    fun response(player: Player, token: String? = null): PlayerActionResponse {
        val heroes = heroRepository.findAllByPlayerIdOrderByStarsDescLevelDescHeroBase_IdAscIdAsc(player.id)
            .map { heroService.asHeroDto(it) }
        val gears = gearRepository.findAllByPlayerIdAndEquippedToIsNull(player.id)
        val jewelries = jewelryRepository.findAllByPlayerId(player.id)
        return PlayerActionResponse(token = token, player = player, heroes = heroes, gears = gears, jewelries = jewelries)
    }

    private fun getHash(name: String, password: String): String {
        return Hashing.sha256().hashString("$name$pwSalt1$password$pwSalt2", StandardCharsets.UTF_8).toString()
    }


}
