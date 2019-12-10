package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Player(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        var name: String,
        @JsonIgnore val email: String,
        @JsonIgnore val password: String,
        val admin: Boolean = false,
        val serviceAccount: Boolean = false,
        var xp: Int = 0,
        var maxXp: Int = 100,
        var level: Int = 1)
