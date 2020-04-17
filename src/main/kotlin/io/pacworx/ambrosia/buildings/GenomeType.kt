package io.pacworx.ambrosia.buildings

import io.pacworx.ambrosia.enums.Rarity

enum class GenomeType(
    val epicChance: Double? = null,
    val rareChance: Double? = null,
    val uncommonChance: Double? = null,
    val commonChance: Double? = null,
    val defaultRarity: Rarity = Rarity.SIMPLE
) {
    SIMPLE_GENOME,
    COMMON_GENOME(defaultRarity = Rarity.COMMON),
    UNCOMMON_GENOME(defaultRarity = Rarity.UNCOMMON),
    RARE_GENOME(defaultRarity = Rarity.RARE),
    EPIC_GENOME(defaultRarity = Rarity.EPIC)
}