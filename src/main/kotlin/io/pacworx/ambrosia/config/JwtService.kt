package io.pacworx.ambrosia.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.pacworx.ambrosia.player.Player
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtService {

    @Value("\${ambrosia.jwt-secret}")
    private lateinit var jwtSecret: String

    fun generateToken(player: Player): String {
        return Jwts.builder()
                .setSubject(player.id.toString())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()
    }

    fun resolvePlayerId(token: String): Long {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.subject.toLong()
    }
}
