package io.pacworx.ambrosia.io.pacworx.ambrosia.dungeons

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class DungeonStage(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val stage: Int,
        val hero1Id: Long?,
        val hero2Id: Long?,
        val hero3Id: Long?,
        val hero4Id: Long?
)
