package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import javax.persistence.*

@Entity
data class PlayerMapTile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(name = "pos_x") val posX: Int,
    @Column(name = "pos_y") val posY: Int,
    var discoverable: Boolean = false,
    var discovered: Boolean = false,
    var victoriousFight: Boolean = false
)