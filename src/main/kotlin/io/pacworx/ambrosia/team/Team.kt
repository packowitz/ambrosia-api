package io.pacworx.ambrosia.team

import io.pacworx.ambrosia.enums.TeamType
import javax.persistence.*

@Entity
data class Team(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING)
    val type: TeamType,
    var hero1Id: Long? = null,
    var hero2Id: Long? = null,
    var hero3Id: Long? = null,
    var hero4Id: Long? = null
)
