package io.pacworx.ambrosia.models

import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Color
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.HeroType
import io.pacworx.ambrosia.io.pacworx.ambrosia.enums.Rarity
import io.pacworx.ambrosia.io.pacworx.ambrosia.models.HeroSkill
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class HeroBase(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @field:NotBlank
    var name: String,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    var rarity: Rarity,
    @field:NotBlank
    var heroClass: String,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    var color: Color,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    var heroType: HeroType,
    var avatar: String = "default",
    var strengthBase: Int = 500,
    var strengthFull: Int = 1000,
    var hpBase: Int = 1000,
    var hpFull: Int = 3000,
    var armorBase: Int = 200,
    var armorFull: Int = 800,
    var initiative: Int = 200,
    var initiativeAsc: Int = 250,
    var crit: Int = 15,
    var critAsc: Int = 20,
    var critMult: Int = 50,
    var critMultAsc: Int = 60,
    var dexterity: Int = 0,
    var dexterityAsc: Int = 20,
    var resistance: Int = 0,
    var resistanceAsc: Int = 20,
    var recruitable: Boolean = false
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "hero_id")
    @OrderBy("number ASC")
    var skills: List<HeroSkill> = ArrayList()
}
