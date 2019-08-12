package io.pacworx.ambrosia.io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.JewelType
import java.lang.RuntimeException
import javax.persistence.*

@Entity
data class Jewelry(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val playerId: Long,
    @Enumerated(EnumType.STRING)
    val type: JewelType,
    var lvl1: Int = 0,
    var lvl2: Int = 0,
    var lvl3: Int = 0,
    var lvl4: Int = 0,
    var lvl5: Int = 0,
    var lvl6: Int = 0,
    var lvl7: Int = 0,
    var lvl8: Int = 0,
    var lvl9: Int = 0,
    var lvl10: Int = 0
) {
    fun getAmount(lvl: Int): Int {
        return when(lvl) {
            1 -> this.lvl1
            2 -> this.lvl2
            3 -> this.lvl3
            4 -> this.lvl4
            5 -> this.lvl5
            6 -> this.lvl6
            7 -> this.lvl7
            8 -> this.lvl8
            9 -> this.lvl9
            10 -> this.lvl10
            else -> throw RuntimeException("Jewel level $lvl is not support")
        }
    }

    fun increaseAmount(lvl: Int, amount: Int) {
        when(lvl) {
            1 -> this.lvl1 += amount
            2 -> this.lvl2 += amount
            3 -> this.lvl3 += amount
            4 -> this.lvl4 += amount
            5 -> this.lvl5 += amount
            6 -> this.lvl6 += amount
            7 -> this.lvl7 += amount
            8 -> this.lvl8 += amount
            9 -> this.lvl9 += amount
            10 -> this.lvl10 += amount
        }
    }
}