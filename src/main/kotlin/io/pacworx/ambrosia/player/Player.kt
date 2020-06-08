package io.pacworx.ambrosia.player

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.hero.Color
import java.time.Instant
import javax.persistence.*

@Entity
data class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var locked: Instant? = null,
    var name: String,
    @JsonIgnore val email: String,
    @JsonIgnore val password: String,
    var lastAction: Instant = Instant.now(),
    var lastLogin: Instant = Instant.now(),
    val admin: Boolean = false,
    val betaTester: Boolean = false,
    val serviceAccount: Boolean = false,
    var xp: Int = 0,
    var maxXp: Int = 100,
    var level: Int = 1,
    @Enumerated(EnumType.STRING)
    var color: Color? = null,
    var currentMapId: Long? = null
) {
    fun didAction() {
        this.lastAction = Instant.now()
    }

    fun didLogin() {
        this.lastLogin = Instant.now()
        this.didAction()
    }
}
