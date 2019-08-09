package io.pacworx.ambrosia.io.pacworx.ambrosia.services

import com.google.common.hash.Hashing
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.PlayerRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets


@Service
class PlayerService(private val playerRepository: PlayerRepository) {

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

    private fun getHash(name: String, password: String): String {
        return Hashing.sha256().hashString("$name$pwSalt1$password$pwSalt2", StandardCharsets.UTF_8).toString()
    }


}
