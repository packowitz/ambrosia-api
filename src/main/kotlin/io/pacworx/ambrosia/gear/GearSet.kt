package io.pacworx.ambrosia.gear

/**
 * To add a new set you need to:
 * - add the set name to this enum
 * - add dynamic properties with the sets name (+ _SET) and the bonuses
 * - add the set name to JewelType
 * - add dynamic properties for the special jewel
 * - add the set (+_SET) and jewel (+_JEWEL) to PropertyType
 */
enum class GearSet {
    STONE_SKIN,
    VITAL_AURA,
    POWER_FIST,
    BUFFS_BLESSING,
    BERSERKERS_AXE,
    MYTHICAL_MIRROR,
    WARHORN,
    REVERSED_REALITY
}
