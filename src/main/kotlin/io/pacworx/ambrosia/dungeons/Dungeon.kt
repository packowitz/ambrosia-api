package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import javax.persistence.*

@Entity
data class Dungeon(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        var name: String,
        val serviceAccountId: Long

) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "dungeon_id")
    @OrderBy("stage ASC")
    var stages: List<DungeonStage> = ArrayList()
}

