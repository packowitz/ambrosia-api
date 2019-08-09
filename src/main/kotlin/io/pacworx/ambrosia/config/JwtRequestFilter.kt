package io.pacworx.ambrosia.io.pacworx.ambrosia.config

import io.pacworx.ambrosia.io.pacworx.ambrosia.models.PlayerRepository
import io.pacworx.ambrosia.io.pacworx.ambrosia.services.JwtService
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
                       private val playerRepository: PlayerRepository): OncePerRequestFilter() {

    private val log = KotlinLogging.logger {}
    private val TOKEN_HEADER = "Authorization"
    private val TOKEN_PREFIX = "Bearer "

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            request.getHeader(TOKEN_HEADER)?.takeIf { it.startsWith(TOKEN_PREFIX) }?.let {
                val playerId = jwtService.resolvePlayerId(it.substring(TOKEN_PREFIX.length))
                val player = playerRepository.getOne(playerId)

                val authorities = mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
                if (player.admin) {
                    authorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))
                }
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(player, null, authorities)
            }
        } catch (e: Exception) {
            log.warn(e.message, e)
        }
        chain.doFilter(request, response)
    }
}
