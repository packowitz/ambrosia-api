package io.pacworx.ambrosia.expedition

import java.time.Instant
import javax.persistence.*

@Entity
data class Expedition(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "expedition_base_id")
    val expeditionBase: ExpeditionBase,
    var active: Boolean,
    val created: Instant,
    val availableUntil: Instant
)