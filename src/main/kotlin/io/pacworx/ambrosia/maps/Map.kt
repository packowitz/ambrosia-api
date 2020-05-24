package io.pacworx.ambrosia.maps

import io.pacworx.ambrosia.story.StoryTrigger
import java.time.LocalDateTime
import javax.persistence.*

@Entity(name = "map")
data class Map(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val startingMap: Boolean = false,
    @Enumerated(EnumType.STRING) val storyTrigger: StoryTrigger? = null,
    val discoverySteamCost: Int = 5,
    @Column(name = "min_x") val minX: Int,
    @Column(name = "max_x") val maxX: Int,
    @Column(name = "min_y") val minY: Int,
    @Column(name = "max_y") val maxY: Int,
    @Enumerated(EnumType.STRING) val background: MapBackground,
    var lastModified: LocalDateTime = LocalDateTime.now()
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "map_id")
    @OrderBy("pos_x ASC, pos_y ASC")
    var tiles: List<MapTile> = ArrayList()
}