package io.pacworx.ambrosia.buildings

import com.fasterxml.jackson.annotation.JsonFormat
import io.pacworx.ambrosia.resources.ResourceType
import io.pacworx.ambrosia.resources.ResourceType.*

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class Trade(
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val giveType: ResourceType,
    val giveAmount: Int,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    val getType: ResourceType,
    val getAmount: Int,
    val negotiationGiveReduction: Int,
    val negotiationGetIncrease: Int
) {
    METAL_TO_IRON(METAL, 50, IRON, 1, 1, 0),
    IRON_TO_METAL(IRON, 1, METAL, 20, 0, 1),
    IRON_TO_STEEL(IRON, 50, STEEL, 1, 1, 0),
    STEEL_TO_IRON(STEEL, 1, IRON, 20, 0, 1),
    
    WOOD_TO_BROWN_COAL(WOOD, 50, BROWN_COAL, 1, 1, 0),
    BROWN_COAL_TO_WOOD(BROWN_COAL, 1, WOOD, 20, 0, 1),
    BROWN_COAL_TO_BLACK_COAL(BROWN_COAL, 50, BLACK_COAL, 1, 1, 0),
    BLACK_COAL_TO_BROWN_COAL(BLACK_COAL, 1, BROWN_COAL, 20, 0, 1);

    fun getName(): String = name
}