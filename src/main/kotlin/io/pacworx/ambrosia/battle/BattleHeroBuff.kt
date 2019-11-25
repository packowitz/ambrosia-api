package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Buff
import javax.persistence.*

@Entity
data class BattleHeroBuff(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Enumerated(EnumType.STRING)
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
