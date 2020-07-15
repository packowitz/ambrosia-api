package io.pacworx.ambrosia.maps

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class PlayerMap (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    var favorite: Boolean = true,
    @ManyToOne
    @JoinColumn(name = "map_id", updatable = false)
    val map: Map,
    var created: LocalDateTime = LocalDateTime.now(),
    var mapCheckedTimestamp: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "player_map_id")
    @OrderBy("pos_x ASC, pos_y ASC")
    var playerTiles: MutableList<PlayerMapTile> = ArrayList()
}