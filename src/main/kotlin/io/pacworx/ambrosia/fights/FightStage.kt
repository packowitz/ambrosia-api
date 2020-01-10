package io.pacworx.ambrosia.fights

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class FightStage(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val stage: Int,
        val hero1Id: Long?,
        val hero2Id: Long?,
        val hero3Id: Long?,
        val hero4Id: Long?
)
