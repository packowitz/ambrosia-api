package io.pacworx.ambrosia.loot

enum class LootBoxType(
    val exactOneSlot: Boolean,
    val enforce100chance: Boolean,
    val resourceRangeAllowed: Boolean,
    val progressStatAllowed: Boolean,
    val gearAllowed: Boolean,
    val singleJewelTypeRequired: Boolean
) {
    LOOT(
        exactOneSlot = false,
        enforce100chance = false,
        resourceRangeAllowed = true,
        progressStatAllowed = true,
        gearAllowed = true,
        singleJewelTypeRequired = false
    ),
    STORY(
        exactOneSlot = false,
        enforce100chance = false,
        resourceRangeAllowed = true,
        progressStatAllowed = true,
        gearAllowed = true,
        singleJewelTypeRequired = false
    ),
    EXPEDITION(
        exactOneSlot = false,
        enforce100chance = false,
        resourceRangeAllowed = true,
        progressStatAllowed = true,
        gearAllowed = true,
        singleJewelTypeRequired = false
    ),
    MERCHANT(
        exactOneSlot = true,
        enforce100chance = false,
        resourceRangeAllowed = true,
        progressStatAllowed = false,
        gearAllowed = true,
        singleJewelTypeRequired = false
    ),
    ODD_JOB(
        exactOneSlot = false,
        enforce100chance = true,
        resourceRangeAllowed = false,
        progressStatAllowed = true,
        gearAllowed = false,
        singleJewelTypeRequired = true
    ),
    ACHIEVEMENT(
        exactOneSlot = false,
        enforce100chance = true,
        resourceRangeAllowed = false,
        progressStatAllowed = true,
        gearAllowed = false,
        singleJewelTypeRequired = true
    ),
    BLACK_MARKET(
        exactOneSlot = true,
        enforce100chance = true,
        resourceRangeAllowed = false,
        progressStatAllowed = false,
        gearAllowed = false,
        singleJewelTypeRequired = true
    ),
    INBOX(
        exactOneSlot = false,
        enforce100chance = true,
        resourceRangeAllowed = false,
        progressStatAllowed = true,
        gearAllowed = false,
        singleJewelTypeRequired = false
    )
}