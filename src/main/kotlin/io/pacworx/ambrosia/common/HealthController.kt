package io.pacworx.ambrosia.common

import io.pacworx.ambrosia.player.PlayerRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("health")
class HealthController(private val playerRepository: PlayerRepository) {

    @GetMapping
    fun health(): HealthStatus =
        HealthStatus(playerRepository.numberOfPlayers() > 0, LocalDateTime.now())

    data class HealthStatus(
        val checkedDatabase: Boolean,
        val timestamp: LocalDateTime
    )

}