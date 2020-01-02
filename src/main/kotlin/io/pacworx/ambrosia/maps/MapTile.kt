package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import javax.persistence.*

@Entity
data class MapTile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "pos_x") val posX: Int,
    @Column(name = "pos_y") val posY: Int,
    val type: MapTileType,
    val redAlwaysRevealed: Boolean = false,
    val greenAlwaysRevealed: Boolean = false,
    val blueAlwaysRevealed: Boolean = false,
    val structure: MapTileStructure? = null,
    val dungeonId: Long? = null,
    val fightIcon: FightIcon? = null,
    val fightRepeatable: Boolean = false,
    val portalToMapId: Long? = null
)