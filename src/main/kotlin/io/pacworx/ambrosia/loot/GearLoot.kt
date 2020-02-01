package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.enums.GearSet
import io.pacworx.ambrosia.enums.GearType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class GearLoot (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @JsonIgnore var setNames: String = "",
    @JsonIgnore var typeNames: String = "",
    val legendaryChance: Int = 0,
    val epicChance: Int = 0,
    val rareChance: Int = 0,
    val uncommonChance: Int = 0,
    val commonChance: Int = 0,
    val statFrom: Int = 0,
    val statTo: Int = 100,
    val specialJewelChance: Int = 0,
    val jewel4chance: Int = 0,
    val jewel3chance: Int = 0,
    val jewel2chance: Int = 0,
    val jewel1chance: Int = 0
) {
    fun getSets(): List<GearSet> {
        return setNames.takeIf { it.isNotEmpty() }?.split(";")?.map { GearSet.valueOf(it) } ?: listOf()
    }

    fun setSets(sets: List<GearSet>) {
        this.setNames = sets.joinToString(separator = ";")
    }

    fun getTypes(): List<GearType> {
        return typeNames.takeIf { it.isNotEmpty() }?.split(";")?.map { GearType.valueOf(it) } ?: listOf()
    }

    fun setTypes(types: List<GearType>) {
        this.typeNames = types.joinToString(separator = ";")
    }
}
