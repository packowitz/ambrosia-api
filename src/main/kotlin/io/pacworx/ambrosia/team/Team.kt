package io.pacworx.ambrosia.team

import javax.persistence.*

@Entity
data class Team(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    val type: String,
    var hero1Id: Long? = null,
    var hero2Id: Long? = null,
    var hero3Id: Long? = null,
    var hero4Id: Long? = null,
    var vehicleId: Long? = null
)
