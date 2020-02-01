package io.pacworx.ambrosia.resources

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.resources.ResourceTypeCategory.*

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ResourceType(val category: ResourceTypeCategory) {
    STEAM(BATTLE_FEE), STEAM_MAX(DEFINITION), PREMIUM_STEAM(PREMIUM_RESOURCE), PREMIUM_STEAM_MAX(DEFINITION),
    COGWHEELS(BATTLE_FEE), COGWHEELS_MAX(DEFINITION), PREMIUM_COGWHEELS(PREMIUM_RESOURCE), PREMIUM_COGWHEELS_MAX(DEFINITION),
    TOKENS(BATTLE_FEE), TOKENS_MAX(DEFINITION), PREMIUM_TOKENS(PREMIUM_RESOURCE), PREMIUM_TOKENS_MAX(DEFINITION),
    COINS(CURRENCY), RUBIES(CURRENCY),
    METAL(BUILDER_MATERIAL), METAL_MAX(DEFINITION), IRON(BUILDER_MATERIAL), IRON_MAX(DEFINITION), STEAL(BUILDER_MATERIAL), STEAL_MAX(DEFINITION),
    WOOD(GEAR_MATERIAL), WOOD_MAX(DEFINITION), BROWN_COAL(GEAR_MATERIAL), BROWN_COAL_MAX(DEFINITION), BLACK_COAL(GEAR_MATERIAL), BLACK_COAL_MAX(DEFINITION),
    SIMPLE_GENOME(HERO_MATERIAL), COMMON_GENOME(HERO_MATERIAL), UNCOMMON_GENOME(HERO_MATERIAL), RARE_GENOME(HERO_MATERIAL), EPIC_GENOME(HERO_MATERIAL)
    ;

    fun getName(): String = name
}