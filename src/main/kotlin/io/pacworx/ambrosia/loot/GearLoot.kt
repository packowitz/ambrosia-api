package io.pacworx.ambrosia.loot

import com.fasterxml.jackson.annotation.JsonIgnore
import io.pacworx.ambrosia.enums.GearSet
import io.pacworx.ambrosia.enums.GearType
import io.pacworx.ambrosia.enums.Rarity
import org.hibernate.annotations.Type
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class GearLoot (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @JsonIgnore var setNames: String?,
    @JsonIgnore var typeNames: String?,
    val legendaryChance: Int = 0,
    val epicChance: Int = 0,
    val rareChance: Int = 0,
    val uncommonChance: Int = 0,
    val commonChance: Int = 0,
    val statFrom: Int,
    val statTo: Int,
    val specialJewelChance: Int = 0,
    val jewel4chance: Int = 0,
    val jewel3chance: Int = 0,
    val jewel2chance: Int = 0,
    val jewel1chance: Int = 0
) {
    fun getSets(): List<GearSet> {
        return setNames?.split(";")?.map { GearSet.valueOf(it) } ?: GearSet.values().asList()
    }

    fun setSets(sets: List<GearSet>) {
        this.setNames = sets.joinToString(separator = ";")
    }

    fun getTypes(): List<GearType> {
        return typeNames?.split(";")?.map { GearType.valueOf(it) } ?: GearType.values().asList()
    }

    fun setTypes(types: List<GearType>) {
        this.typeNames = types.joinToString(separator = ";")
    }
}
