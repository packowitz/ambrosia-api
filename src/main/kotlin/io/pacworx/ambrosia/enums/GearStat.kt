package io.pacworx.ambrosia.io.pacworx.ambrosia.enums

enum class GearStat(val range: Map<Rarity, Pair<Int, Int>>) {
    HP_ABS(mapOf(
            Rarity.SIMPLE to Pair(10, 25),
            Rarity.COMMON to Pair(20, 45),
            Rarity.UNCOMMON to Pair(40, 85),
            Rarity.RARE to Pair(75, 125),
            Rarity.EPIC to Pair(115, 190),
            Rarity.LEGENDARY to Pair(175, 250)
    )),
    HP_PERC(mapOf(
            Rarity.SIMPLE to Pair(5, 8),
            Rarity.COMMON to Pair(7, 15),
            Rarity.UNCOMMON to Pair(13, 28),
            Rarity.RARE to Pair(23, 43),
            Rarity.EPIC to Pair(32, 65),
            Rarity.LEGENDARY to Pair(53, 84)
    )),
    ARMOR_ABS(mapOf(
            Rarity.SIMPLE to Pair(10, 25),
            Rarity.COMMON to Pair(20, 45),
            Rarity.UNCOMMON to Pair(40, 85),
            Rarity.RARE to Pair(75, 125),
            Rarity.EPIC to Pair(115, 190),
            Rarity.LEGENDARY to Pair(175, 250)
    )),
    ARMOR_PERC(mapOf(
            Rarity.SIMPLE to Pair(5, 8),
            Rarity.COMMON to Pair(7, 15),
            Rarity.UNCOMMON to Pair(13, 28),
            Rarity.RARE to Pair(23, 43),
            Rarity.EPIC to Pair(32, 65),
            Rarity.LEGENDARY to Pair(53, 84)
    )),
    STRENGTH_ABS(mapOf(
            Rarity.SIMPLE to Pair(10, 25),
            Rarity.COMMON to Pair(20, 45),
            Rarity.UNCOMMON to Pair(40, 85),
            Rarity.RARE to Pair(75, 125),
            Rarity.EPIC to Pair(115, 190),
            Rarity.LEGENDARY to Pair(175, 250)
    )),
    STRENGTH_PERC(mapOf(
            Rarity.SIMPLE to Pair(5, 8),
            Rarity.COMMON to Pair(7, 15),
            Rarity.UNCOMMON to Pair(13, 28),
            Rarity.RARE to Pair(23, 43),
            Rarity.EPIC to Pair(32, 65),
            Rarity.LEGENDARY to Pair(53, 84)
    )),
    CRIT(mapOf(
            Rarity.SIMPLE to Pair(8, 22),
            Rarity.COMMON to Pair(18, 25),
            Rarity.UNCOMMON to Pair(21, 30),
            Rarity.RARE to Pair(25, 36),
            Rarity.EPIC to Pair(32, 42),
            Rarity.LEGENDARY to Pair(37, 50)
    )),
    CRIT_MULT(mapOf(
            Rarity.SIMPLE to Pair(5, 8),
            Rarity.COMMON to Pair(7, 15),
            Rarity.UNCOMMON to Pair(13, 28),
            Rarity.RARE to Pair(23, 43),
            Rarity.EPIC to Pair(32, 65),
            Rarity.LEGENDARY to Pair(53, 84)
    )),
    INITIATIVE(mapOf(
            Rarity.SIMPLE to Pair(5, 17),
            Rarity.COMMON to Pair(10, 19),
            Rarity.UNCOMMON to Pair(13, 22),
            Rarity.RARE to Pair(18, 25),
            Rarity.EPIC to Pair(21, 29),
            Rarity.LEGENDARY to Pair(25, 34)
    )),
    SPEED(mapOf(
            Rarity.SIMPLE to Pair(5, 17),
            Rarity.COMMON to Pair(10, 19),
            Rarity.UNCOMMON to Pair(13, 22),
            Rarity.RARE to Pair(18, 25),
            Rarity.EPIC to Pair(21, 29),
            Rarity.LEGENDARY to Pair(25, 34)
    )),
    RESISTANCE(mapOf(
            Rarity.SIMPLE to Pair(5, 8),
            Rarity.COMMON to Pair(7, 15),
            Rarity.UNCOMMON to Pair(13, 28),
            Rarity.RARE to Pair(23, 43),
            Rarity.EPIC to Pair(32, 65),
            Rarity.LEGENDARY to Pair(53, 84)
    )),
    DEXTERITY(mapOf(
            Rarity.SIMPLE to Pair(5, 8),
            Rarity.COMMON to Pair(7, 15),
            Rarity.UNCOMMON to Pair(13, 28),
            Rarity.RARE to Pair(23, 43),
            Rarity.EPIC to Pair(32, 65),
            Rarity.LEGENDARY to Pair(53, 84)
    ))
}
