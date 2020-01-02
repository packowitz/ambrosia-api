package io.pacworx.ambrosia.io.pacworx.ambrosia.maps

import javax.persistence.*

@Entity(name = "map")
data class Map(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val startingMap: Boolean = false,
    @Column(name = "min_x") val minX: Int,
    @Column(name = "max_x") val maxX: Int,
    @Column(name = "min_y") val minY: Int,
    @Column(name = "max_y") val maxY: Int
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "map_id")
    @OrderBy("pos_x ASC, pos_y ASC")
    var tiles: List<MapTile> = ArrayList()
}