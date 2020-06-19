package io.pacworx.ambrosia.hero

import io.pacworx.ambrosia.hero.skills.HeroSkill
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
    val startingHero: Boolean = false,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    var heroType: HeroType,
    var avatar: String = "default",
    var strengthBase: Int = 300,
    var strengthFull: Int = when (rarity) {
        Rarity.SIMPLE -> 630
        Rarity.COMMON -> 680
        Rarity.UNCOMMON -> 740
        Rarity.RARE -> 800
        Rarity.EPIC -> 880
        else -> throw RuntimeException("Highest hero rarity is EPIC")
    },
    var hpBase: Int = 1000,
    var hpFull: Int = when (rarity) {
        Rarity.SIMPLE -> 2900
        Rarity.COMMON -> 3100
        Rarity.UNCOMMON -> 3300
        Rarity.RARE -> 3500
        Rarity.EPIC -> 3800
        else -> throw RuntimeException("Highest hero rarity is EPIC")
    },
    var armorBase: Int = 600,
    var armorFull: Int = when (rarity) {
        Rarity.SIMPLE -> 1300
        Rarity.COMMON -> 1400
        Rarity.UNCOMMON -> 1500
        Rarity.RARE -> 1600
        Rarity.EPIC -> 1800
        else -> throw RuntimeException("Highest hero rarity is EPIC")
    },
    var initiative: Int = 200,
    var initiativeAsc: Int = 250,
    var crit: Int = 10,
    var critAsc: Int = 20,
    var critMult: Int = 40,
    var critMultAsc: Int = 60,
    var dexterity: Int = 0,
    var dexterityAsc: Int = 20,
    var resistance: Int = 0,
    var resistanceAsc: Int = 20,
    var recruitable: Boolean = false,
    val maxAscLevel: Int = when (rarity) {
        Rarity.SIMPLE -> 2
        Rarity.COMMON -> 4
        Rarity.UNCOMMON -> 6
        Rarity.RARE -> 8
        Rarity.EPIC -> 10
        else -> throw RuntimeException("Highest hero rarity is EPIC")
    }
) {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "hero_id")
    @OrderBy("number ASC")
    var skills: List<HeroSkill> = ArrayList()

    fun getSkill(skillNumber: Int): HeroSkill? = skills.find { it.number == skillNumber }
}
