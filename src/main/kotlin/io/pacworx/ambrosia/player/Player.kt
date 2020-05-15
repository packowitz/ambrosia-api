package io.pacworx.ambrosia.player

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.hero.Color
import javax.persistence.*

@Entity
data class Player(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
    var name: String,
    @JsonIgnore val email: String,
    @JsonIgnore val password: String,
    val admin: Boolean = false,
    val betaTester: Boolean = false,
    val serviceAccount: Boolean = false,
    var xp: Int = 0,
    var maxXp: Int = 100,
    var level: Int = 1,
    @Enumerated(EnumType.STRING)
        var color: Color? = null,
    var currentMapId: Long? = null
)
