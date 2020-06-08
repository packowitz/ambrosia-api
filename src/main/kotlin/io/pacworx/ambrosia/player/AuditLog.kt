package io.pacworx.ambrosia.player

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class AuditLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    val action: String,
    val error: Boolean = false,
    val adminAction: Boolean = false,
    val betaTesterAction: Boolean = false,
    val timestamp: Instant = Instant.now()
)
