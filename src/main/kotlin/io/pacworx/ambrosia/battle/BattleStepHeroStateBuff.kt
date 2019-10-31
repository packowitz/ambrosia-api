package io.pacworx.ambrosia.io.pacworx.ambrosia.battle

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Buff
import javax.persistence.*

@Entity
data class BattleStepHeroStateBuff(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @Enumerated(EnumType.STRING)
        val buff: Buff,
        var intensity: Int,
        var duration: Int
) {
        fun getType(): String = buff.type.name
}
