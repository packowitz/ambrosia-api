package io.pacworx.ambrosia.player

import io.pacworx.ambrosia.common.PlayerActionResponse
import io.pacworx.ambrosia.config.JwtService
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("auth")
class AuthController(private val playerService: PlayerService,
                     private val jwtService: JwtService) {

    @PostMapping("register")
    @Transactional
    fun register(@RequestBody @Valid request: RegisterRequest): PlayerActionResponse {
        val player = playerService.signup(request.name, request.email, request.password)
        return playerService.response(player, jwtService.generateToken(player))
    }

    @PostMapping("login")
    @Transactional
    fun login(@RequestBody @Valid request: LoginRequest): PlayerActionResponse {
        val player = playerService.login(request.email, request.password)
        return playerService.response(player, jwtService.generateToken(player))
    }

    @GetMapping("pwhash/{email}/{password}")
    fun pwHash(@PathVariable("email") email: String, @PathVariable("password") password: String): String {
        return playerService.getHash(email, password)
    }

}

data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)
