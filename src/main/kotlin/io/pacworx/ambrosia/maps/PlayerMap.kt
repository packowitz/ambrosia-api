package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class PlayerMap (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @ManyToOne
    @JoinColumn(name = "map_id", updatable = false)
    val map: Map,
    var mapCheckedTimestamp: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "player_map_id")
    @OrderBy("pos_x ASC, pos_y ASC")
    var playerTiles: List<PlayerMapTile> = ArrayList()
}