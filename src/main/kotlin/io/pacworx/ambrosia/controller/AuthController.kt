package io.pacworx.ambrosia.io.pacworx.ambrosia.controller

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.Player
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.JwtService
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.PlayerService
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("auth")
class AuthController(private val playerService: PlayerService,
                     private val jwtService: JwtService) {

    @PostMapping("register")
    fun register(@RequestBody @Valid request: RegisterRequest): TokenResponse {
        val player = playerService.signup(request.name, request.email, request.password)
        return TokenResponse(jwtService.generateToken(player), player)
    }

    @PostMapping("login")
    fun login(@RequestBody @Valid request: LoginRequest): TokenResponse {
        val player = playerService.login(request.email, request.password)
        return TokenResponse(jwtService.generateToken(player), player)
    }
}

data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

data class TokenResponse(val token: String, val player: Player)
