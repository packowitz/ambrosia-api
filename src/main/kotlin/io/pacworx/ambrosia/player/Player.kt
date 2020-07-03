package io.pacworx.ambrosia.player

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.hero.Color
import java.time.Instant
import javax.persistence.*

@Entity
data class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @JsonIgnore var locked: Instant? = null,
    var name: String,
    @JsonIgnore val email: String,
    @JsonIgnore val password: String,
    @JsonIgnore var lastLogin: Instant = Instant.now(),
    val admin: Boolean = false,
    val betaTester: Boolean = false,
    val serviceAccount: Boolean = false,
    @Enumerated(EnumType.STRING)
    var color: Color? = null
) {

    fun didLogin() {
        this.lastLogin = Instant.now()
    }
}
