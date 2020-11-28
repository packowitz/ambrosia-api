package io.pacworx.ambrosia.achievements

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class PlayerTask(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    val taskClusterId: Long,
    var currentTaskNumber: Int
)