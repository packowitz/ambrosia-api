package io.pacworx.ambrosia.config

import io.pacworx.ambrosia.exceptions.UnauthenticatedException
import io.pacworx.ambrosia.player.Player
import io.pacworx.ambrosia.player.PlayerService
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtRequestFilter(private val jwtService: JwtService,
                       private val playerService: PlayerService): OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}
    private val TOKEN_HEADER = "Authorization"
    private val TOKEN_PREFIX = "Bearer "

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        var player: Player? = null
        var playerLocked = false
        try {
            request.getHeader(TOKEN_HEADER)?.takeIf { it.startsWith(TOKEN_PREFIX) }?.let {
                val playerId = jwtService.resolvePlayerId(it.substring(TOKEN_PREFIX.length))
                player = if (request.method in listOf("POST", "PUT", "DELETE")) {
                    playerLocked = true
                    playerService.getAndLock(playerId) ?: throw UnauthenticatedException("${request.method} ${request.requestURI} - Unknown or locked player with id $playerId")
                } else {
                    playerService.get(playerId)
                        ?: throw UnauthenticatedException("${request.method} ${request.requestURI} - Unknown player with id $playerId")
                }

                val authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
                if (player?.admin == true) {
                    authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
                }
                if (player?.betaTester == true) {
                    authorities.add(SimpleGrantedAuthority("ROLE_BETA_TESTER"))
                }
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(player, null, authorities)
                request.setAttribute("player", player)
            }
        } catch (e: Exception) {
            log.warn(e.message)
        }
        chain.doFilter(request, response)
        if (playerLocked) {
            player?.let { playerService.releaseLock(it.id) }
        }
    }
}
