package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import javax.persistence.*

@Entity
data class MapTile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "pos_x") val posX: Int,
    @Column(name = "pos_y") val posY: Int,
    @Enumerated(EnumType.STRING) val type: MapTileType,
    val redAlwaysRevealed: Boolean = false,
    val greenAlwaysRevealed: Boolean = false,
    val blueAlwaysRevealed: Boolean = false,
    @Enumerated(EnumType.STRING) val structure: MapTileStructure? = null,
    val fightId: Long? = null,
    @Enumerated(EnumType.STRING) val fightIcon: FightIcon? = null,
    val fightRepeatable: Boolean = false,
    val portalToMapId: Long? = null
)