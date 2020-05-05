package io.pacworx.ambrosia.battle

import com.fasterxml.jackson.annotation.JsonFormat
import javax.persistence.*

@Entity
data class BattleHeroBuff(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val buff: Buff,
    var intensity: Int,
    var duration: Int,
    val sourceHeroId: Long,
    var value: Int? = null,
    var resistance: Int
) {

    open fun decreaseDuration() {
        duration--
    }

    fun getType(): String = buff.type.name
}
